package tw.waterballsa.utopia.jda

import ch.qos.logback.core.util.OptionHelper.getEnv
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections
import org.reflections.scanners.Scanners.*
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import tw.waterballsa.utopia.jda.JdaInstance.compositeListener
import java.lang.reflect.Method

val log = KotlinLogging.logger {}

internal class CompositeListener : EventListener {
    internal val listeners: MutableList<UtopiaListener> = mutableListOf()

    override fun onEvent(event: GenericEvent) {
        for (listener in listeners) {
            listener.onEvent(event)
        }
    }

    fun register(e: UtopiaListener) {
        log.debug { "[Register UtopiaListener] {\"name\":\"${e.name}\"}" }
        listeners.add(e)
    }

    fun unregister(e: UtopiaListener) {
        log.debug { "[Unregister UtopiaListener] {\"name\":\"${e.name}\"}" }
        listeners.remove(e)
    }

}

private object JdaInstance {
    val compositeListener = CompositeListener()
    val instance: JDA by lazy {
        val env = getEnv("BOT_TOKEN").trim()
        val builder = JDABuilder.createDefault(env)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                )
                .addEventListeners(compositeListener)
        builder.build()
    }
}

@Configuration
open class JdaConfig {
    @Bean
    open fun jda(): JDA {
        return JdaInstance.instance
    }
}

open class UtopiaListener : EventListener {
    var name: String? = null
    val declarations: MutableMap<Class<out GenericEvent>, (GenericEvent.() -> Unit)> = mutableMapOf()

    override fun onEvent(e: GenericEvent) {
        declarations[e.javaClass]?.invoke(e)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : GenericEvent> on(noinline listenerDeclaration: T.() -> Unit) {
        val pair = T::class.java to listenerDeclaration
        declarations[pair.first] = pair.second as GenericEvent.() -> Unit
    }
}

private fun registerListener(e: UtopiaListener): Unit = compositeListener.register(e)

fun listener(listenerDeclaration: UtopiaListener.() -> Unit): UtopiaListener {
    val listener = UtopiaListener()
    listenerDeclaration.invoke(listener)
    return listener
}

internal fun loadListenersFromAllUtopiaModules(context: ApplicationContext): List<UtopiaListener> {
    val listeners = mutableListOf<UtopiaListener>()

    val reflections = Reflections("tw.waterballsa.utopia", SubTypes, TypesAnnotated, MethodsReturn)
    val listenerFunctions = reflections.get(MethodsReturn.with(UtopiaListener::class.java).`as`(Method::class.java))
            .filterNot {
                it.declaringClass.`package`.name.startsWith("tw.waterballsa.utopia.jda")
            }

    for (listenerFunction in listenerFunctions) {
        val parameterTypes = listenerFunction.parameterTypes
        val parameters = arrayOfNulls<Any>(parameterTypes.size)
        parameterTypes.forEachIndexed { i, parameterType ->
            parameters[i] = context.getBean(parameterType)
        }
        val listener = listenerFunction.invoke(null, *parameters) as UtopiaListener
        listener.name = listenerFunction.name
        listeners.add(listener)
    }

    return listeners
}

@ComponentScan(basePackages = ["tw.waterballsa.utopia"])
@Configuration
open class Config

fun runJda(context: ApplicationContext) {
    val listeners = loadListenersFromAllUtopiaModules(context)
    for (listener in listeners) {
        registerListener(listener)
    }
    val e: SlashCommandInteractionEvent

    JdaInstance.instance.awaitReady()
}
