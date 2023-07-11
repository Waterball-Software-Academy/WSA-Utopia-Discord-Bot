package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"


open class WsaDiscordProperties(properties: Properties) {
    val engineerLifeChannelId: String
    val engineerLifeChannelLink: String
    val careerAdvancementTopicChannelId: String
    val careerAdvancementTopicChannelLink: String
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
    val discussionAreaChannelId: String
    val unlockEntryChannelLink: String
    val selfIntroChannelLink: String
    val discussionAreaChannelLink: String
    val flagPostChannelId: String
    val flagPostChannelLink: String
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
            discussionAreaChannelId = getProperty("wsa-discussion-area-channel-id")
            unlockEntryChannelLink = getProperty("wsa-unlock-entry-channel-link")
            selfIntroChannelLink = getProperty("wsa-self-intro-channel-link")
            discussionAreaChannelLink = getProperty("wsa-discussion-area-channel-link")
            flagPostChannelId = getProperty("wsa-flag-post-channel-id")
            flagPostChannelLink = getProperty("wsa-flag-post-channel-link")
            careerAdvancementTopicChannelId = getProperty("career-advancement-topic-channel-id")
            careerAdvancementTopicChannelLink = getProperty("career-advancement-topic-channel-link")
            engineerLifeChannelId = getProperty("engineer-life-channel-id")
            engineerLifeChannelLink = getProperty("engineer-life-channel-link")
            mongoDatabase = getProperty("mongo-database")
        }
    }
}
