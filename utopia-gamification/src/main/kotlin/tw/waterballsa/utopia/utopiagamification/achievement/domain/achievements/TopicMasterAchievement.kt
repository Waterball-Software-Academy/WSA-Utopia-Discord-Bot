package tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Name.TOPIC_MASTER
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.Action
import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward


class TopicMasterAchievement(
    condition: Condition,
    rule: Rule,
    reward: Reward
) : Achievement(
    TOPIC_MASTER,
    TEXT_MESSAGE,
    condition,
    rule,
    reward
) {

    class Condition : Achievement.Condition {
        override fun meet(action: Action): Boolean = true
    }
}
