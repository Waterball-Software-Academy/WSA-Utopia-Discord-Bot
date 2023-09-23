package tw.waterballsa.utopia.utopiagmification.quest

import tw.waterballsa.utopia.utopiagamification.quest.domain.Action
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest

class MissionTestCase(
    val displayName: String,
    val player: Player,
    val quest: Quest,
    val action: Action,
    val isMatchAction: Boolean,
    val isMissionCompleted: Boolean
)
