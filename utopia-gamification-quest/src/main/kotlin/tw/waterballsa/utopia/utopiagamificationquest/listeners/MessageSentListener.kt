package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

@Component
class MessageSentListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) {
                return
            }

            val user = author
            val player = user.toPlayer() ?: return

            val action = MessageSentAction(
                player,
                (channel as? ThreadChannel)?.parentChannel?.id ?: channel.id,
                message.contentDisplay,
                message.referencedMessage != null,
                message.attachments.any { it.isImage },
                (channel as? VoiceChannel)?.members?.size ?: 0
            )

            playerFulfillMissionsService.execute(action, user.presenter)
        }
    }
}
