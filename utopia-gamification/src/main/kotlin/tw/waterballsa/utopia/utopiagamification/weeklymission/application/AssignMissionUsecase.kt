package tw.waterballsa.utopia.utopiagamification.weeklymission.application

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.WeeklyMission
import kotlin.random.Random

@Component
class AssignMissionUsecase(
        val missionRepository: MissionRepository,
        val weeklyMissionRepository: WeeklyMissionRepository
) {

    fun execute() {
        val playerIds = missionRepository.findAllByQuestId(10)
                .filter { it.isCompleted() }
                .map{ it.player.id }

        playerIds.forEach {
            val randomWeeklyMissions = createRandomWeeklyMission(it)
            randomWeeklyMissions.forEach {
                weeklyMissionRepository.save(it)
            }
        }
    }

    private fun createRandomWeeklyMission(playerId :String) : List<WeeklyMission> {
        val weeklyMissions = emptyList<WeeklyMission>()
        val random = Random(10)
        val whichType = random.nextInt(2)

        return weeklyMissions
    }
}
