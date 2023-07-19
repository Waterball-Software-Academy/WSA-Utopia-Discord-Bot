package tw.waterballsa.utopia.utopiagamificationquest.service

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository.Query


@Component
class ClaimMissionRewardService(
    private val missionRepository: MissionRepository
) {
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val mission = missionRepository.findMission(Query(player.id, true, questTitle)) ?: return
            mission.rewardPlayer()
            missionRepository.saveMission(mission)

            mission.nextMission()?.let { nextMission ->
                missionRepository.saveMission(nextMission)
                presenter.presentNextMission(nextMission)
            }
            presenter.presentMission(mission)
        }
    }

    class Request(
        val player: Player,
        val questTitle: String
    )

    interface Presenter {
        fun presentMission(mission: Mission)
        fun presentNextMission(mission: Mission)
    }
}

