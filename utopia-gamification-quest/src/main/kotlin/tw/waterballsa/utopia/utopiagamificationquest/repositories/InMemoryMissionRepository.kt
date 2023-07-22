package tw.waterballsa.utopia.utopiagamificationquest.repositories

import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import java.util.*

//class ImMemoryMissionRepository : MissionRepository {
//
//    private val missions = hashMapOf<UUID, Mission>()
//
//    override fun findMission(query: MissionRepository.Query): Mission? =
//            missions.values.find { query.match(it) }
//
//    override fun findIncompleteMissionsByPlayerId(playerId: String): List<Mission> =
//            missions.values.filter { it.player.id == playerId && !it.isCompleted() }
//
//    override fun findAllByPlayerId(playerId: String): List<Mission> =
//            missions.values.filter { it.player.id == playerId }
//
//    override fun saveMission(mission: Mission): Mission {
//        missions[mission.id] = mission
//        return mission
//    }
//
//}
