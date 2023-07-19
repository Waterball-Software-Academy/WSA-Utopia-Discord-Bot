package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import java.util.*

interface MissionRepository {
    fun findMission(query: Query): Mission?
    fun findIncompleteMissionsByPlayerId(playerId: String): List<Mission>
    fun findAllByPlayerId(playerId: String): List<Mission>
    fun saveMission(mission: Mission): Mission

    class Query(
        private val playerId: String,
        private val isCompleted: Boolean = false,
        private val questTitle: String? = null
    ) {

        fun match(mission: Mission): Boolean =
            mission.player.id == playerId
                    && isCompleted == mission.isCompleted()
                    && questTitle?.equals(mission.quest.title) ?: true
    }
}
