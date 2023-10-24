package tw.waterballsa.utopia.utopiagamification.achievement.domain.actions

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Progression
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type
import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

open class Action(
    val type: Type,
    val player: Player
) {

    fun achieve(achievement: Achievement, progression: Progression): AchievementAchievedEvent? =
        achievement.achieve(player, progression)
}
