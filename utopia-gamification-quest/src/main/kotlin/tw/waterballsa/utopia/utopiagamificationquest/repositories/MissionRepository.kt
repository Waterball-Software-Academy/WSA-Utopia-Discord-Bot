package tw.waterballsa.utopia.utopiagamificationquest.repositories

import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission

interface MissionRepository {

    fun findPlayerMissionByQuestId(playerId: String, questId: Int): Mission?
    fun findInProgressMissionsByPlayerId(playerId: String): List<Mission>
    fun findAllByPlayerId(playerId: String): List<Mission>
    fun findAllByQuestId(questId: Int): List<Mission>
    fun saveMission(mission: Mission): Mission
}
