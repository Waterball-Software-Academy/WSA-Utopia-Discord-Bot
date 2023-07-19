package tw.waterballsa.utopia.utopiagamificationquest.domain

import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.MissionDocument
import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.State
import java.util.*
import java.util.UUID.randomUUID

class Mission(val id: UUID, val player: Player, val quest: Quest, state: State) {
    constructor(player: Player, quest: Quest) : this(randomUUID(), player, quest, State.IN_PROGRESS)

    var state: State = state
        private set

    fun match(action: Action): Boolean = action.match(quest.criteria)

    fun carryOut(action: Action) {
        action.execute(quest.criteria)
    }

    fun isCompleted(): Boolean {
        if (quest.criteria.isCompleted) {
            state = State.COMPLETED
        }
        return quest.criteria.isCompleted
    }

    fun rewardPlayer() {
        player.gainExp(quest.reward.exp)
        state = State.CLAIMED
    }

    fun nextMission(): Mission? {
        if (isCompleted().not()) {
            return null
        }
        return quest.nextQuest?.let { Mission(player, it) }
    }

    fun toDocument(): MissionDocument {
        return MissionDocument(id.toString(), player.id, quest.id, "test", state)
    }
}
