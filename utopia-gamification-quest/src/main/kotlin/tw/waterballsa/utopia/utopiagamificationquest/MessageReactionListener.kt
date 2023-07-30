package tw.waterballsa.utopia.utopiagamificationquest

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

@Component
class MessageReactionListener(
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
    private val playerRepository: PlayerRepository
) : UtopiaListener() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            val player = user ?: return
            val action = action ?: return

            playerFulfillMissionsService.execute(action, player.presenter)
        }
    }

    private val MessageReactionAddEvent.action
        get() = member?.let {
            MessageReactionAction(
                it.toPlayer(),
                messageId,
                emoji.name
            )
        }

    private val User.presenter
        get() = object : PlayerFulfillMissionsService.Presenter {
            override fun presentClaimMissionReward(mission: Mission) {
                claimMissionReward(mission)
            }
        }

    private fun Member.toPlayer(): Player =
        playerRepository.findPlayerById(id) ?: playerRepository.savePlayer(
            Player(
                id,
                user.effectiveName,
                ULong.MIN_VALUE,
                1u,
                roles.map { it.id }.toMutableList(),
                timeJoined
            )
        )
}
