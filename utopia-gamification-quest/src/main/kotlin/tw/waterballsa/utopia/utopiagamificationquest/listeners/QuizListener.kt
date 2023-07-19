package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.QuizEndEvent
import tw.waterballsa.utopia.jda.domains.QuizPreparationStartEvent
import tw.waterballsa.utopia.jda.domains.UtopiaEvent
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.QuizAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.quizQuest
import tw.waterballsa.utopia.utopiagamificationquest.extensions.log
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

@Component
class QuizListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
    private val missionRepository: MissionRepository,
    private val quests: Quests,
    private val jda: JDA
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onUtopiaEvent(event: UtopiaEvent) {
        when (event) {
            is QuizPreparationStartEvent -> onQuizPreparationStart(event)
            is QuizEndEvent -> onQuizEnd(event)
        }
    }

    private fun onQuizPreparationStart(event: QuizPreparationStartEvent) {
        with(event) {
            if (!hasInProgressQuizQuest()) {
                reply("沒有考試任務，不能開始考試")
                return
            }

            startQuiz()
        }
    }

    private fun QuizPreparationStartEvent.hasInProgressQuizQuest(): Boolean =
        missionRepository.findInProgressMissionsByPlayerId(quizTakerId)
            .any { it.quest.title == quests.quizQuest.title }

    private fun onQuizEnd(event: QuizEndEvent) {
        val user = jda.retrieveUserById(event.quizTakerId).complete() ?: return
        val player = user.toPlayer() ?: return

        val action = QuizAction(
            player,
            event.quizName,
            event.score
        )

        playerFulfillMissionsService.execute(action, user.claimMissionRewardPresenter)
    }
}
