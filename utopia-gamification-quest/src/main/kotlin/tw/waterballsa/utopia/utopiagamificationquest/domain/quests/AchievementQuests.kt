package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentCriteria
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward

val Quests.participateInDiscussionQuest: Quest
    get() = quest {
        title = "participate in discussion"
        description = "在閒聊區留五次留言"
        reward = Reward(
            "已達成五次留言!!",
            100u,
        )
        criteria = MessageSentCriteria(wsa.discussionAreaChannelId, 5)
    }
