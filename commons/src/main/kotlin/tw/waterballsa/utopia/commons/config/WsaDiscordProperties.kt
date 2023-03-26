package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"

open class WsaDiscordProperties(properties: Properties) {
    val guildId: String
    val unlockEntryMessageId: String
    val selfIntroChannelId: String
    val wsaGuestRoleId: String
    val wsaCitizenRoleId: String

    init {
        logger.info { properties }
        guildId = properties.getProperty("guild-id")
        unlockEntryMessageId = properties.getProperty("unlock-entry-message-id")
        selfIntroChannelId = properties.getProperty("self-intro-channel-id")
        wsaGuestRoleId = properties.getProperty("wsa-guest-role-id")
        wsaCitizenRoleId = properties.getProperty("wsa-citizen-role-id")
    }
}

