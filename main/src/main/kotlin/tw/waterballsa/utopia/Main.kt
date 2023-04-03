package tw.waterballsa.utopia


import ch.qos.logback.core.util.OptionHelper
import org.springframework.context.annotation.*
import tw.waterballsa.utopia.commons.config.ENV_BETA
import tw.waterballsa.utopia.commons.config.ENV_PROD
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.utils.createDirectoryIfNotExists
import tw.waterballsa.utopia.commons.utils.loadProperties
import tw.waterballsa.utopia.jda.runJda
import java.io.File
import java.util.TimeZone

@Configuration
@ComponentScan("tw.waterballsa.utopia")
open class MyDependencyInjectionConfig {
    @Bean
    open fun commonAnnotationBeanPostProcessor(): CommonAnnotationBeanPostProcessor {
        return CommonAnnotationBeanPostProcessor()
    }

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
}

private const val DATABASE_DIRECTORY = "data"

fun main() {
    val context = AnnotationConfigApplicationContext(MyDependencyInjectionConfig::class.java)
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"))
    File(DATABASE_DIRECTORY).createDirectoryIfNotExists()
    runJda(context)
}


