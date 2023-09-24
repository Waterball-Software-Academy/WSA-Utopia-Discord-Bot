package tw.waterballsa.utopia.gamification.quest.service

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.quest.domain.Mission
import tw.waterballsa.utopia.gamification.quest.domain.Player
import tw.waterballsa.utopia.gamification.quest.domain.Quest
import tw.waterballsa.utopia.gamification.repositories.MissionRepository


@Component
class PlayerAcceptQuestService(
    private val missionRepository: MissionRepository
) {

    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            if (isMissionAcquired()) {
                presenter.presentPlayerHasAcquiredMission()
                return
            }
            val mission = missionRepository.saveMission(Mission(player, quest))
            presenter.presentPlayerAcquiresMission(mission)
        }
    }

    private fun Request.isMissionAcquired(): Boolean =
        missionRepository.findPlayerMissionByQuestId(player.id, quest.id) != null

    class Request(
        val player: Player,
        val quest: Quest
    )

    interface Presenter {
        fun presentPlayerHasAcquiredMission()
        fun presentPlayerAcquiresMission(mission: Mission)
    }
}
