package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

@Component
class MessageReactionListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            val user = user ?: return
            val player = user.toPlayer() ?: return

            val action = MessageReactionAction(
                player,
                messageId,
                emoji.name
            )

            playerFulfillMissionsUsecase.execute(action, user.claimMissionRewardPresenter)
        }
    }
}
