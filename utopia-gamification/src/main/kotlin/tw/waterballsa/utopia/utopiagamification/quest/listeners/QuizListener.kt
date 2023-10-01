package tw.waterballsa.utopia.utopiagamification.quest.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.QuizEndEvent
import tw.waterballsa.utopia.jda.domains.QuizPreparationStartEvent
import tw.waterballsa.utopia.jda.domains.UtopiaEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.QuizAction
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound

private const val quizQuestId = 10

private val log = KotlinLogging.logger {}

@Component
class QuizListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase,
    private val missionRepository: MissionRepository,
    private val questRepository: QuestRepository,
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

    private fun QuizPreparationStartEvent.hasInProgressQuizQuest(): Boolean {
        val quizQuest = questRepository.findById(quizQuestId)
            ?: throw notFound(Quest::class)
                .id(quizQuestId)
                .message("check player has quiz quest")
                .build()

        return missionRepository.findInProgressMissionsByPlayerId(quizTakerId)
            .any { it.quest.title == quizQuest.title }
    }

    private fun onQuizEnd(event: QuizEndEvent) {
        with(event) {
            val user = jda.retrieveUserById(quizTakerId).complete() ?: return
            val player = user.toPlayer() ?: return

            val action = QuizAction(
                player,
                quizName,
                correctCount
            )

            log.info { """[quiz end] { quizTakerId : "$quizTakerId", quizName : "$quizName", correctCount : "$correctCount" } """ }

            playerFulfillMissionsUsecase.execute(action, user.claimMissionRewardPresenter)
        }
    }
}
