package tw.waterballsa.utopia.jda

import ch.qos.logback.core.util.OptionHelper.getEnv
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.reflections.Reflections
import org.reflections.scanners.Scanners.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
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
                        GatewayIntent.SCHEDULED_EVENTS,
                )
                .enableCache(CacheFlag.SCHEDULED_EVENTS)
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
    val listenerDeclarations: MutableMap<Class<out GenericEvent>, (GenericEvent.() -> Unit)> = mutableMapOf()
    val commands = mutableListOf<CommandData>()

    override fun onEvent(e: GenericEvent) {
        listenerDeclarations[e.javaClass]?.invoke(e)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : GenericEvent> on(noinline listenerDeclaration: T.() -> Unit) {
        val pair = T::class.java to listenerDeclaration
        listenerDeclarations[pair.first] = pair.second as GenericEvent.() -> Unit
    }

    fun command(commandDeclaration: () -> CommandData) {
        commands.add(commandDeclaration.invoke())
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
        val parameters = listenerFunction.parameters
        val arguments = arrayOfNulls<Any>(parameters.size)

        parameters.forEachIndexed { i, p ->
            arguments[i] = if (p.isAnnotationPresent(Qualifier::class.java)) {
                val qualifier = p.getAnnotation(Qualifier::class.java)
                val beanName = qualifier.value
                context.getBean(beanName)
            } else {
                context.getBean(p.type)
            }
        }
        val listener = listenerFunction.invoke(null, *arguments) as UtopiaListener
        listener.name = listenerFunction.name
        listeners.add(listener)
    }

    return listeners
}

const val WSA_GUILD_BEAN_NAME = "WsaGuild"

fun runJda() {
    JdaInstance.instance.awaitReady()
}

fun registerAllJdaListeners(context: AnnotationConfigApplicationContext) {
    val wsa = context.getBean(WSA_GUILD_BEAN_NAME, Guild::class.java)

    val listeners = loadListenersFromAllUtopiaModules(context)
    for (listener in listeners) {
        registerListener(listener)
        if (listener.commands.isNotEmpty()) {
            wsa.updateCommands().addCommands(listener.commands).complete()
        }
    }
}
