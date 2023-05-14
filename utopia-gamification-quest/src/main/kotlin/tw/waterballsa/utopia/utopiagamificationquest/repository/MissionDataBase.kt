package tw.waterballsa.utopia.utopiagamificationquest.repository

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import java.util.UUID

@Repository
class MissionDataBase {

    private val missions = hashMapOf<UUID, Mission>()

    fun findMissionsByPlayerId(playerId: String): List<Mission> {
        return missions.filter { it.value.player.id == playerId }.values.toList()
    }

    fun saveMission(mission: Mission) {
        missions[mission.id] = mission
    }

    fun removeMission(mission: Mission) {
        missions.remove(mission.id)
    }

    private fun Mission.toData(): MissionData {
        return MissionData(id, player.id, quest)
    }
}

class MissionData(
        private val missionId: UUID,
        val playerId: String,
        private val quest: Quest
) {

    fun toDomain(player: Player): Mission {
        return Mission(missionId, player, quest)
    }
}
