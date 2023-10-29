package tw.waterballsa.utopia.utopiagamification.achievement.domain.events

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

class AchievementAchievedEvent(
    val reward: Reward,
)
