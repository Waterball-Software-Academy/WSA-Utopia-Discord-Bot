package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"

open class WsaDiscordProperties(properties: Properties) {
    val guildId: ULong
    val unlockEntryMessageId: ULong
    val selfIntroChannelId: ULong
    val wsaGuestRoleId: ULong
    val wsaCitizenRoleId: ULong

    init {
        logger.info { properties }
        guildId = properties.getProperty("guild-id").toULong()
        unlockEntryMessageId = properties.getProperty("unlock-entry-message-id").toULong()
        selfIntroChannelId = properties.getProperty("self-intro-channel-id").toULong()
        wsaGuestRoleId = properties.getProperty("wsa-guest-role-id").toULong()
        wsaCitizenRoleId = properties.getProperty("wsa-citizen-role-id").toULong()
    }
}

