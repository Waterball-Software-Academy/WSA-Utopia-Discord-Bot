package tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Name.LONG_ARTICLE
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.Action
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.SendMessageAction
import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

class LongArticleAchievement(
    condition: Condition,
    rule: Rule,
    reward: Reward
) : Achievement(
    LONG_ARTICLE,
    TEXT_MESSAGE,
    condition,
    rule,
    reward
) {

    /**
     * LongArticle.Condition：判斷此次發送的訊息字數是否達到 800 字
     */
    class Condition(
        private val wordLength: Int
    ) : Achievement.Condition {
        override fun meet(action: Action): Boolean =
            action is SendMessageAction && action.contentWordRequirement(wordLength)
    }
}
