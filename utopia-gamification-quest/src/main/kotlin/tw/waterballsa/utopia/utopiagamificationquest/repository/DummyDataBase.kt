package tw.waterballsa.utopia.utopiagamificationquest.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission

//mogoDB也會有
//一個表 對應 一個repo，看領域模型
//拆出playerData 、 MissionData
//在同時轉成 domain mission
//orm
@Repository
class DummyDataBase(
        @Autowired private val playerRepository: PlayerDataBase,
        @Autowired private val missionRepository: MissionDataBase
) {

    fun findMissionsByPlayerId(playerId: String): List<Mission> {
        val player = playerRepository.findPlayerById(playerId) ?: return listOf()
        return missionRepository.findMissionsByPlayer(player)
    }

    fun saveMission(mission: Mission) {
        if (mission.isNewMission()) {
            playerRepository.putIfAbsent(mission.player)
        } else {
            playerRepository.savePlayer(mission.player)
        }
        missionRepository.saveMission(mission)
    }

    fun removeMission(mission: Mission) {
        playerRepository.savePlayer(mission.player)
        missionRepository.removeMission(mission)
    }
}



