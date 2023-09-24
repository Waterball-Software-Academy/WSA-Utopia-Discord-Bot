package tw.waterballsa.utopia.gamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.quest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.gamification.repositories.PlayerRepository
import tw.waterballsa.utopia.gamification.quest.service.PlayerFulfillMissionsService

@Component
class MessageReactionListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
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

            playerFulfillMissionsService.execute(action, user.claimMissionRewardPresenter)
        }
    }
}
