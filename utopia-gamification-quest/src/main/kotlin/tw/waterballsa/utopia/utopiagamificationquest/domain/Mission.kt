package tw.waterballsa.utopia.utopiagamificationquest.domain

import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.State
import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.State.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID

class Mission(
    val id: UUID,
    val player: Player,
    val quest: Quest,
    var state: State,
    var completedTime: LocalDateTime?
) {
    constructor(player: Player, quest: Quest) : this(randomUUID(), player, quest, IN_PROGRESS, null)

    fun match(action: Action): Boolean = action.match(quest.criteria)

    fun carryOut(action: Action) {
        if (action.execute(quest.criteria)) {
            state = COMPLETED
            completedTime = now()
        }
    }

    fun isCompleted(): Boolean = state == CLAIMED || state == COMPLETED

    fun rewardPlayer() {
        player.gainExp(quest.reward.exp)
        state = CLAIMED
    }

    fun nextMission(): Mission? {
        if (isCompleted().not()) {
            return null
        }
        return quest.nextQuest?.let { Mission(player, it) }
    }
}
