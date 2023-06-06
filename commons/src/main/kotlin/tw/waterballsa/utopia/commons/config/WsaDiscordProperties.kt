package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"


open class WsaDiscordProperties(properties: Properties) {
    val knowledgeKingChannelId: String
    val guildId: String
    val unlockEntryMessageId: String
    val selfIntroChannelId: String
    val wsaGuestRoleId: String
    val wsaCitizenRoleId: String
    val gentlemenForumCategoryId: String
    val wsaGentlemenBroadcastChannelId: String
    val wsaPartyChannelId: String
    val wsaGaaSConversationChannelId: String
    val wsaGaaSMemberRoleId: String
    val wsaAlphaRoleId: String
    val mongoDatabase: String

    init {
        properties.run {
            logger.info { properties }
            guildId = getProperty("guild-id")
            unlockEntryMessageId = getProperty("unlock-entry-message-id")
            selfIntroChannelId = getProperty("self-intro-channel-id")
            wsaGuestRoleId = getProperty("wsa-guest-role-id")
            wsaCitizenRoleId = getProperty("wsa-citizen-role-id")
            gentlemenForumCategoryId = getProperty("wsa-gentlemen-forum-category-id")
            wsaGentlemenBroadcastChannelId = getProperty("wsa-gentlemen-broadcast-channel-id")
            wsaPartyChannelId = getProperty("wsa-party-channel-id")
            wsaGaaSConversationChannelId = getProperty("wsa-gaas-conversation-channel-id")
            wsaGaaSMemberRoleId = getProperty("wsa-gaas-member-role-id")
            wsaAlphaRoleId = getProperty("wsa-alpha-role-id")
            knowledgeKingChannelId = getProperty("knowledge-king-channel-id")
            mongoDatabase = getProperty("mongo-database")
        }
    }
}
