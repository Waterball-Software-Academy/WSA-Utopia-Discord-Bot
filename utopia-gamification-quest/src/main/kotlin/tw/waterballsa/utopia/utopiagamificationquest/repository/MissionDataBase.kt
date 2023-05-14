package tw.waterballsa.utopia.utopiagamificationquest.repository

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest

@Repository
class MissionDataBase {
    
    private val missions = hashMapOf<Int, MissionData>()
    private val idPool = IdPool()

    fun findMissionsByPlayer(player: Player): List<Mission> {
        val missionData = missions.filter { it.value.playerId == player.id }
        return missionData.map { it.value.toDomain(player) }
    }

    fun saveMission(mission: Mission) {
        with(mission) {
            if (isNewMission()) {
                missionId = idPool.nextId()
            }
            missions[missionId] = toData()
        }
    }

    fun removeMission(mission: Mission) {
        with(mission) {
            missions.remove(missionId)
            idPool.recycleId(missionId)
        }

    }

    private fun Mission.toData(): MissionData {
        return MissionData(player.id, quest, missionId)
    }
}

class MissionData(
        val playerId: String,
        private val quest: Quest,
        private val missionId: Int
) {

    fun toDomain(player: Player): Mission {
        return Mission(player, quest, missionId)
    }
}

class IdPool {

    private val availableIds = generateSequence(1) { it + 1 }.iterator()
    private val recycledIds = mutableListOf<Int>()

    fun nextId(): Int {
        return if (recycledIds.isNotEmpty()) {
            recycledIds.removeAt(recycledIds.lastIndex)
        } else {
            availableIds.next()
        }
    }

    fun recycleId(id: Int) {
        recycledIds.add(id)
    }
}
