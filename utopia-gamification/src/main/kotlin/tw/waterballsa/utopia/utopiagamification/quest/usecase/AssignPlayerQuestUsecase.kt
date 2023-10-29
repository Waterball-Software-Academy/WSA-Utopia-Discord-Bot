package tw.waterballsa.utopia.utopiagamification.quest.usecase

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.AssignedQuestException
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException


@Component
class AssignPlayerQuestUsecase(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
    private val questRepository: QuestRepository
) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            if (missionRepository.existMission(playerId, questId)) {
                throw AssignedQuestException(playerId, questId)
            }

            val player = playerRepository.findPlayerById(playerId) ?: throw NotFoundException
                .notFound(Player::class)
                .id(playerId)
                .message("can't assign quest.")
                .build()

            val quest = questRepository.findById(questId) ?: throw NotFoundException
                .notFound(Quest::class)
                .id(questId)
                .message("can't be assigned to the player")
                .build()

            val mission = missionRepository.saveMission(Mission(player, quest))

            presenter.presentMission(mission)
        }
    }

    data class Request(
        val playerId: String,
        val questId: Int
    )

    interface Presenter {
        fun presentMission(mission: Mission)
    }
}
