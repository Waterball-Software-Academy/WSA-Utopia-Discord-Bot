package tw.waterballsa.utopia.gamification.quest

import tw.waterballsa.utopia.gamification.quest.domain.Action
import tw.waterballsa.utopia.gamification.quest.domain.Player
import tw.waterballsa.utopia.gamification.quest.domain.Quest

class MissionTestCase(
    val displayName: String,
    val player: Player,
    val quest: Quest,
    val action: Action,
    val isMatchAction: Boolean,
    val isMissionCompleted: Boolean
)
