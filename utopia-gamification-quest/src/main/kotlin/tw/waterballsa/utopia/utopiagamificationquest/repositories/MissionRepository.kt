package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import java.util.*

interface MissionRepository {
    fun findMission(query: Query): Mission?
    fun findIncompletedMissionsByPlayerId(playerId: String): List<Mission>
    fun saveMission(mission: Mission): Mission
    fun removeMission(mission: Mission)

    class Query(private val playerId: String,
                private val isCompleted: Boolean = false,
                private val questTitle: String? = null) {

        fun match(mission: Mission): Boolean {
            return mission.player.id == playerId
                    && isCompleted == mission.isCompleted()
                    && questTitle?.equals(mission.quest.title) ?: true
        }
    }
}

@Repository
class ImMemoryMissionRepository : MissionRepository {

    private val missions = hashMapOf<UUID, Mission>()

    override fun findMission(query: MissionRepository.Query): Mission? =
            missions.values.find { query.match(it) }

    override fun findIncompletedMissionsByPlayerId(playerId: String): List<Mission> =
            missions.values.filter { it.player.id == playerId && !it.isCompleted() }

    override fun saveMission(mission: Mission): Mission {
        missions[mission.id] = mission
        return mission
    }

    override fun removeMission(mission: Mission) {
        missions.remove(mission.id)
    }

    private fun Mission.toData(): MissionData = MissionData(id, player.id, quest)
}

class MissionData(
        private val missionId: UUID,
        val playerId: String,
        private val quest: Quest
) {

    fun toDomain(player: Player): Mission = Mission(missionId, player, quest)
}