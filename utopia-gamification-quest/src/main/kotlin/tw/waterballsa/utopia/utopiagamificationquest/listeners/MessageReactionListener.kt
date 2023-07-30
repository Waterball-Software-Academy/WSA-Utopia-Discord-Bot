package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.listeners.UtopiaGamificationListener
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

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

            playerFulfillMissionsService.execute(action, user.presenter)
        }
    }
}
