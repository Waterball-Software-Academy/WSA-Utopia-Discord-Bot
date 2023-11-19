package tw.waterballsa.utopia.utopiagamification.quest.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.State.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID

class Mission(
    val id: UUID,
    val player: Player,
    val quest: Quest,
    state: State,
    var completedTime: LocalDateTime?
) {

    constructor(player: Player, quest: Quest) : this(randomUUID(), player, quest, IN_PROGRESS, null)

    var state: State = state
        private set

    fun match(action: Action): Boolean = action.match(quest.criteria)

    fun carryOut(action: Action) {
        if (action.execute(quest.criteria)) {
            state = COMPLETED
            completedTime = now()
        }
    }

    fun isCompleted(): Boolean = state == COMPLETED

    fun rewardPlayer() {
        player.gainExp(quest.reward.exp)
        state = CLAIMED
    }

    fun nextQuestId(): Int? {
        if (state == IN_PROGRESS) {
            return null
        }

        return quest.nextQuestId
    }
}

enum class State {
    IN_PROGRESS,
    COMPLETED,
    CLAIMED
}
