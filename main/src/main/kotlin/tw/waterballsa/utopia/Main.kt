package tw.waterballsa.utopia


import ch.qos.logback.core.util.OptionHelper
import mu.KotlinLogging
import tw.waterballsa.utopia.commons.config.ENV_BETA
import tw.waterballsa.utopia.commons.config.ENV_PROD
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.utils.loadProperties
import tw.waterballsa.utopia.jda.runJda

val log = KotlinLogging.logger {}

fun main() {
    val env = OptionHelper.getEnv("DEPLOYMENT_ENV") ?: throw IllegalStateException("DEPLOYMENT_ENV environment variable is not set")
    logger.info { "DEPLOYMENT_ENV=$env" }

    val properties = when (env) {
        ENV_BETA -> loadProperties("wsa.beta.properties")
        ENV_PROD -> loadProperties("wsa.prod.properties")
        else -> throw IllegalArgumentException("doesn't support the env name ${env}.")
    }

    runJda(WsaDiscordProperties(properties))
}


