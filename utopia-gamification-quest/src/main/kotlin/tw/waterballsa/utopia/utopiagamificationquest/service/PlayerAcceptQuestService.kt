package tw.waterballsa.utopia.utopiagamificationquest.service

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository


@Component
class PlayerAcceptQuestService(
    private val missionRepository: MissionRepository
) {

    fun execute(request: Request, presenter: (mission: Mission) -> Unit) {
        with(request) {
            val mission = missionRepository.saveMission(Mission(player, quest))
            presenter(mission)
        }
    }

    class Request(
        val player: Player,
        val quest: Quest
    )
}
