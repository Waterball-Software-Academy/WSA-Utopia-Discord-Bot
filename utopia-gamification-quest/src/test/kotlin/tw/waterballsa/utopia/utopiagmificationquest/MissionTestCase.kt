package tw.waterballsa.utopia.utopiagmificationquest

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest

class MissionTestCase(
    val displayName: String,
    val player: Player,
    val quest: Quest,
    val action: Action,
    val isMatchAction: Boolean,
    val isMissionCompleted: Boolean
)
