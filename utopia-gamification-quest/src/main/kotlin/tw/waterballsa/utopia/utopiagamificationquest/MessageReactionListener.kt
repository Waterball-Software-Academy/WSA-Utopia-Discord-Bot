package tw.waterballsa.utopia.utopiagamificationquest

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService


@Component
class MessageReactionListener(
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
) : UtopiaListener() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            val player = user ?: return
            val action = action ?: return
            playerFulfillMissionsService.execute(action) { completedMission ->
                player.claimMissionReward(completedMission)
            }
        }
    }

    private val MessageReactionAddEvent.action
        get() = user?.let {
            MessageReactionAction(
                Player(it.id, it.name),
                messageId,
                emoji.name
            )
        }
}
