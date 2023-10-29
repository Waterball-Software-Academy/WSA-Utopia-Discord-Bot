package tw.waterballsa.utopia.utopiagamification.achievement.domain.actions

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

open class Action(
    val type: Type,
    val player: Player
)
