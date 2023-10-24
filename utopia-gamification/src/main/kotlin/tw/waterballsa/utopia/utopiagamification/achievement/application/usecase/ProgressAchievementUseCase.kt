package tw.waterballsa.utopia.utopiagamification.achievement.application.usecase

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.achievement.application.presenter.Presenter
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.AchievementRepository
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.ProgressionRepository
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.Action
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.SendMessageAction
import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound

@Component
class ProgressAchievementUseCase(
    private val progressionRepository: ProgressionRepository,
    private val achievementRepository: AchievementRepository,
    private val playerRepository: PlayerRepository
) {

    /**
     * Usecase flow:
     * 1. (DB) find progressions by playerId and achievement Type
     * 2. create an action
     * 3. progress the action, return an event
     *    3-1. achievement progress action, return the result of the action (Boolean)
     *    3-2. if meet achievement condition, refresh the progression (count++)
     *    3-3. achievement achieve progression, return an event
     *         3-3-1. if the progression count achieve the Achievement Rule, return an event
     *         3-3-2. reward.reward(player)
     * 4. (DB) persist the progression and player
     * 5. present the events
     */
    fun execute(request: Request, presenter: Presenter) {
        with(request) {
            // 查
            val player = findPlayer()
            val achievements = achievementRepository.findByType(type)
            val achievementNameToProgression = achievementNameToProgression()

            // 改
            val action = toAction(player)
            val events = achievements.mapNotNull { achievement ->
                val progression = achievementNameToProgression[achievement.name]
                action.progress(achievement, progression)
            }

            // 存
            playerRepository.savePlayer(player)

            // 推
            presenter.present(events)
        }
    }

    private fun Request.findPlayer(): Player =
            playerRepository.findPlayerById(playerId) ?: throw notFound(Player::class).id(playerId).build()

    private fun Request.achievementNameToProgression(): Map<Achievement.Name, Achievement.Progression> =
            progressionRepository.findByPlayerIdAndAchievementType(playerId, type)
                    .associateBy { it.name }

    private fun Request.toAction(player: Player): Action {
        return if (type == TEXT_MESSAGE) {
            SendMessageAction(player, message)
        } else {
            throw IllegalArgumentException("This achievement type '$type' is undefined.")
        }
    }

    private fun Action.progress(achievement: Achievement, progression: Achievement.Progression?): AchievementAchievedEvent? {
        val refreshedProgression = progressionRepository.save(achievement.progressAction(this, progression))
        return achievement.achieve(player, refreshedProgression)
    }

    data class Request(
            val playerId: String,
            val type: Achievement.Type,
            val message: String
    )

}
