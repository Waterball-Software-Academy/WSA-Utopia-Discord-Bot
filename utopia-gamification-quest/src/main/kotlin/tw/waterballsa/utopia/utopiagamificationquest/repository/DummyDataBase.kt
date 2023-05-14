package tw.waterballsa.utopia.utopiagamificationquest.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

@Repository
class DummyDataBase(
        @Autowired private val playerRepository: PlayerDataBase,
        @Autowired private val missionRepository: MissionDataBase
) {

    fun findMissionsByPlayerId(playerId: String): List<Mission> {
        return missionRepository.findMissionsByPlayerId(playerId)
    }

    fun saveMission(mission: Mission) {
        missionRepository.saveMission(mission)
    }

    fun removeMission(mission: Mission) {
        missionRepository.removeMission(mission)
    }

    fun savePlayer(newPlayer: Player): Player {
        return playerRepository.savePlayer(newPlayer)
    }
}



