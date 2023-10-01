package tw.waterballsa.utopia.utopiagamification.quest.usecase

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound

@Component
class ClaimMissionRewardUsecase(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
    private val questRepository: QuestRepository
) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            val mission = missionRepository.findPlayerMissionByQuestId(player.id, questId.toInt()) ?: return

            if (!mission.isCompleted()) {
                presenter.presentRewardsNotAllowed(mission)
                return
            }

            mission.rewardPlayer()

            val player = playerRepository.savePlayer(mission.player)
            missionRepository.saveMission(mission)

            presenter.presentPlayerExpNotification(mission)

            val nextQuestId = mission.nextQuestId() ?: return

            val nextQuest = questRepository.findById(nextQuestId)
                ?: throw notFound(Quest::class)
                    .id(nextQuestId)
                    .message("assign player next quest id")
                    .build()

            missionRepository.saveMission(Mission(player, nextQuest))
            presenter.presentNextMission(Mission(player, nextQuest))
        }
    }

    class Request(
        val player: Player,
        val questId: String
    )

    interface Presenter {
        fun presentPlayerExpNotification(mission: Mission)
        fun presentNextMission(mission: Mission)
        fun presentRewardsNotAllowed(mission: Mission)
    }
}
