package tw.waterballsa.utopia.utopiagamification.achievement.application.usecase

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.achievement.application.presenter.Presenter
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.AchievementRepository
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.ProgressionRepository
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.*
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
            val player = playerRepository.findPlayerById(playerId) ?: throw notFound(Player::class).id(playerId).build()
            val progressions = progressionRepository.findByPlayerIdAndAchievementType(playerId, type)
            val achievements = achievementRepository.findByType(type)

            val action = request.toAction(player)

            // 改
            val events = achievements.mapNotNull { achievement ->
                player.progress(action, progressions, achievement)
            }

            // 存
            playerRepository.savePlayer(player)

            // 推
            presenter.present(events)
        }
    }

    data class Request(
        val playerId: String,
        val type: Type,
        val message: String
    ) {
        fun toAction(player: Player): Action {
            if (type == TEXT_MESSAGE) {
                return SendMessageAction(player, message)
            } else {
                throw IllegalArgumentException("This achievement type '$type' is undefined.")
            }
        }
    }

    private fun Player.progress(
        action: Action,
        progressions: Map<Name, Progression>,
        achievement: Achievement,
    ): AchievementAchievedEvent? {

        val progression = progressions.findProgressionByAchievement(achievement)
        val refreshedProgression = achievement.progressAction(action, progression)

        progressionRepository.save(refreshedProgression)

        return achievement.achieve(this, refreshedProgression)
    }

    private fun Map<Name, Progression>.findProgressionByAchievement(achievement: Achievement): Progression? =
        this[achievement.name]
}
