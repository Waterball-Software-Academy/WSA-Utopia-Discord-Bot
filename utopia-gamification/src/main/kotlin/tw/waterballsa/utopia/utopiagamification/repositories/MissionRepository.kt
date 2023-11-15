package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission

interface MissionRepository {

    fun findPlayerMissionByQuestId(playerId: String, questId: Int): Mission?
    fun findInProgressMissionsByPlayerId(playerId: String): List<Mission>
    fun findAllByPlayerId(playerId: String): List<Mission>
    fun findAllByQuestId(questId: Int): List<Mission>
    fun saveMission(mission: Mission): Mission
    fun existMission(playerId: String, questId: Int): Boolean
}
