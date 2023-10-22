package tw.waterballsa.utopia

import ch.qos.logback.core.util.OptionHelper
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.springframework.beans.factory.config.ConfigurableBeanFactory.*
import org.springframework.context.annotation.*
import tw.waterballsa.utopia.commons.config.ENV_BETA
import tw.waterballsa.utopia.commons.config.ENV_PROD
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.extensions.createDirectoryIfNotExists
import tw.waterballsa.utopia.commons.utils.loadProperties
import tw.waterballsa.utopia.document.generateCommandTableMarkdown
import tw.waterballsa.utopia.jda.WSA_GUILD_BEAN_NAME
import tw.waterballsa.utopia.jda.registerAllJdaListeners
import tw.waterballsa.utopia.jda.runJda
import java.io.File
import java.util.*

private const val DATABASE_DIRECTORY = "data"

@Configuration
@ComponentScan("tw.waterballsa.utopia")
open class MyDependencyInjectionConfig {

    @Bean
    open fun commonAnnotationBeanPostProcessor(): CommonAnnotationBeanPostProcessor =
            CommonAnnotationBeanPostProcessor()

    @Bean
    open fun objectMapper(): ObjectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    open fun wsaProperties(): WsaDiscordProperties {
        val env = OptionHelper.getEnv("DEPLOYMENT_ENV")
                ?: throw IllegalStateException("DEPLOYMENT_ENV environment variable is not set")
        logger.info { "DEPLOYMENT_ENV=$env" }

        val properties = when (env) {
            ENV_BETA -> loadProperties("wsa.beta.properties")
            ENV_PROD -> loadProperties("wsa.prod.properties")
            else -> throw IllegalArgumentException("doesn't support the env name ${env}.")
        }
        return WsaDiscordProperties(properties)
    }

    @Bean(WSA_GUILD_BEAN_NAME)
    open fun wsaGuild(wsaProperties: WsaDiscordProperties, jda: JDA): Guild =
            jda.getGuildById(wsaProperties.guildId)
                    ?: throw RuntimeException("You must run JDA before instantiating the ApplicationContext.")

    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    open fun timer(): Timer = Timer()
}

fun main() {
    runJda()
    val context = AnnotationConfigApplicationContext(MyDependencyInjectionConfig::class.java)
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"))
    File(DATABASE_DIRECTORY).createDirectoryIfNotExists()
    registerAllJdaListeners(context)
    generateCommandTableMarkdown(context, "wsa-bot-commands.md")
}
