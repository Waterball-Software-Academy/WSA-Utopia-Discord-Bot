package tw.waterballsa.utopia.utopiagamificationquest.repositories

import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission

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
