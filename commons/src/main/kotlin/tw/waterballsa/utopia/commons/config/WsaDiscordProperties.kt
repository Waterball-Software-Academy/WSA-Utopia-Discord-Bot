package tw.waterballsa.utopia.commons.config

import ch.qos.logback.core.util.OptionHelper.getEnv
import me.jakejmattson.discordkt.annotations.Service
import tw.waterballsa.utopia.commons.utils.loadProperties

@Service
class WsaDiscordProperties() {
    private var guildId: String
    private var unlockEntryMessageId: String

    init {
        val properties = when (val env = getEnv("deployment.env")) {
            "beta" -> loadProperties("wsa.beta.properties")
            "prod" -> loadProperties("wsa.prod.properties")
            else -> throw IllegalArgumentException("doesn't support the env name ${env}.")
        }

        guildId = properties.getProperty("guild-id")
        unlockEntryMessageId = properties.getProperty("unlock-entry-message-id")
    }
}

