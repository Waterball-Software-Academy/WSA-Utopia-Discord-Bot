package tw.waterballsa.utopia.commons.config

import mu.KotlinLogging
import java.util.*

val logger = KotlinLogging.logger {}
const val ENV_BETA = "beta"
const val ENV_PROD = "prod"


open class WsaDiscordProperties(properties: Properties) {

    // Database Name
    val mongoDatabase: String

    // Guild ID
    val guildId: String

    // Channel ID
    val wsaGentlemenBroadcastChannelId: String
    val wsaPartyChannelId: String
    val wsaGaaSConversationChannelId: String
    val knowledgeKingChannelId: String
    val careerAdvancementTopicChannelId: String
    val engineerLifeChannelId: String
    val unlockEntryChannelId: String
    val selfIntroChannelId: String
    val discussionAreaChannelId: String
    val flagPostChannelId: String
    val resumeCheckChannelId: String
    val featuredVideosChannelId: String
    val wsaGuideLineChannelId: String

    // Role ID
    val wsaGuestRoleId: String
    val wsaCitizenRoleId: String
    val wsaAlphaRoleId: String
    val wsaGaaSMemberRoleId: String
    val wsaSECSubsriberMemberRoleId: String
    val wsaGaaSSubscriberRoleId: String
    val wsaPurefuncSubscriberRoleId: String
    val wsaTTMSubscriberRoleId: String
    val wsaCSLabSubscriberRoleId: String
    val wsaLongArticleRoleId: String
    val wsaTopicMasterRoleId: String

    // Message ID
    val unlockEntryMessageId: String
    val manageSubscriberRoleMessageId : String

    // Forum ID
    val flagPostGuideId: String
    val gentlemenForumCategoryId: String

    // Post ID
    val waterBallJournalPostId: String
    val waterBallLoseWeightPostId: String

    init {
        properties.run {
            logger.info { properties }

            // Database Name
            mongoDatabase = getProperty("mongo-database")

            // Guild ID
            guildId = getProperty("guild-id")

            // Channel ID
            wsaGentlemenBroadcastChannelId = getProperty("wsa-gentlemen-broadcast-channel-id")
            wsaPartyChannelId = getProperty("wsa-party-channel-id")
            wsaGaaSConversationChannelId = getProperty("wsa-gaas-conversation-channel-id")
            knowledgeKingChannelId = getProperty("knowledge-king-channel-id")
            careerAdvancementTopicChannelId = getProperty("career-advancement-topic-channel-id")
            engineerLifeChannelId = getProperty("engineer-life-channel-id")
            unlockEntryChannelId = getProperty("unlock-entry-channel-id")
            selfIntroChannelId = getProperty("self-intro-channel-id")
            discussionAreaChannelId = getProperty("wsa-discussion-area-channel-id")
            flagPostChannelId = getProperty("wsa-flag-post-channel-id")
            resumeCheckChannelId = getProperty("resume-check-channel-id")
            featuredVideosChannelId = getProperty("featured-videos-channel-id")
            wsaGuideLineChannelId = getProperty("wsa-guideline-channel-id")

            // Role ID
            wsaGuestRoleId = getProperty("wsa-guest-role-id")
            wsaCitizenRoleId = getProperty("wsa-citizen-role-id")
            wsaAlphaRoleId = getProperty("wsa-alpha-role-id")
            wsaGaaSMemberRoleId = getProperty("wsa-gaas-member-role-id")
            wsaSECSubsriberMemberRoleId = getProperty("wsa-sec-subscriber-role-id")
            wsaGaaSSubscriberRoleId = getProperty("wsa-gaas-subscriber-role-id")
            wsaPurefuncSubscriberRoleId = getProperty("wsa-purefunc-subscriber-role-id")
            wsaTTMSubscriberRoleId = getProperty("wsa-ttm-subscriber-role-id")
            wsaCSLabSubscriberRoleId = getProperty("wsa-cslab-subscriber-role-id")
            wsaLongArticleRoleId = getProperty("wsa-long-article-role-id")
            wsaTopicMasterRoleId = getProperty("wsa-topic-master-role-id")

            // Message ID
            unlockEntryMessageId = getProperty("unlock-entry-message-id")
            manageSubscriberRoleMessageId = getProperty("manage-subscriber-role-message-id")

            // Forum ID
            flagPostGuideId = getProperty("flag-post-guide-id")
            gentlemenForumCategoryId = getProperty("wsa-gentlemen-forum-category-id")

            // Post ID
            waterBallJournalPostId = getProperty("water-ball-journal-post-id")
            waterBallLoseWeightPostId = getProperty("water-ball-lose-weight-post-id")
        }
    }
}

