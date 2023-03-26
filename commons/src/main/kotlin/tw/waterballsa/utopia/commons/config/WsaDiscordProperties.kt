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
    var gentlemanForumIds: Array<String>

    init {
        properties.run {
            logger.info { properties }
            guildId = getProperty("guild-id")
            unlockEntryMessageId = getProperty("unlock-entry-message-id")
            selfIntroChannelId = getProperty("self-intro-channel-id")
            wsaGuestRoleId = getProperty("wsa-guest-role-id")
            wsaCitizenRoleId = getProperty("wsa-citizen-role-id")
            gentlemanForumIds = properties.getProperty("wsa-gentle-forum-ids")
                    .split(",").toTypedArray()
        }
    }
}
