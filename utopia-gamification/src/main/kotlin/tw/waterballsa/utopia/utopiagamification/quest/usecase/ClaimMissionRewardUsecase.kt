package tw.waterballsa.utopia.utopiagamification.quest.usecase

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.ClaimInProgressMissionException
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.ClaimedMissionException
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound

@Component
class ClaimMissionRewardUsecase(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val mission =
                missionRepository.findPlayerMissionByQuestId(playerId, questId) ?: throw notFound(Mission::class)
                    .id("{ playerId:$playerId, questId:$questId}")
                    .message("can't claim reward to player")
                    .build()

            if (mission.isInProgress()) {
                throw ClaimInProgressMissionException(mission.quest.title)
            }

            if (mission.isClaimed()) {
                throw ClaimedMissionException(mission.quest.title)
            }

            mission.rewardPlayer()

            playerRepository.savePlayer(mission.player)
            missionRepository.saveMission(mission)

            presenter.presentPlayerExpNotification(mission)
        }
    }

    data class Request(
        val playerId: String,
        val questId: Int
    )

    interface Presenter {
        fun presentPlayerExpNotification(mission: Mission)
    }
}
