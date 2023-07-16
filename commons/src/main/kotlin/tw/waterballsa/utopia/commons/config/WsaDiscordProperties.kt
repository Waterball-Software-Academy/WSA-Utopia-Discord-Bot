package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"


open class WsaDiscordProperties(properties: Properties) {
    val engineerLifeChannelId: String
    val careerAdvancementTopicChannelId: String
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
    val unlockEntryChannelId: String
    val flagPostChannelId: String
    val mongoDatabase: String
    val resumeCheckChannelId: String
    val featuredVideosChannelId: String
    val flagPostGuideId: String
    val waterBallJournalPostId: String
    val waterBallLoseWeightPostId: String
    val wsaGuideLineChannelId: String

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
            unlockEntryChannelId = getProperty("unlock-entry-channel-id")
            flagPostChannelId = getProperty("wsa-flag-post-channel-id")
            careerAdvancementTopicChannelId = getProperty("career-advancement-topic-channel-id")
            engineerLifeChannelId = getProperty("engineer-life-channel-id")
            mongoDatabase = getProperty("mongo-database")
            resumeCheckChannelId = getProperty("resume-check-channel-id")
            featuredVideosChannelId = getProperty("featured-videos-channel-id")
            flagPostGuideId = getProperty("flag-post-guide-id")
            waterBallJournalPostId = getProperty("water-ball-journal-post-id")
            waterBallLoseWeightPostId = getProperty("water-ball-lose-weight-post-id")
            wsaGuideLineChannelId = getProperty("wsa-guideline-channel-id")
        }
    }
}
