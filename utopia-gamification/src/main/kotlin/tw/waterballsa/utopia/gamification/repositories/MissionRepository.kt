package tw.waterballsa.utopia.gamification.repositories

import tw.waterballsa.utopia.gamification.quest.domain.Mission

interface MissionRepository {

    fun findPlayerMissionByQuestId(playerId: String, questId: Int): Mission?
    fun findInProgressMissionsByPlayerId(playerId: String): List<Mission>
    fun findAllByPlayerId(playerId: String): List<Mission>
    fun findAllByQuestId(questId: Int): List<Mission>
    fun saveMission(mission: Mission): Mission
}
