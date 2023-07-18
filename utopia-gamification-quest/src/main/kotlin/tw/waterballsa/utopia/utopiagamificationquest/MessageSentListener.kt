package tw.waterballsa.utopia.utopiagamificationquest

import dev.minn.jda.ktx.messages.MessageCreateBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands.user
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

@Component
class MessageSentListener(
    private val playerFulfillMissionsService: PlayerFulfillMissionsService
) : UtopiaListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) {
                return
            }

            val player = author
            playerFulfillMissionsService.execute(action) { completedMission ->
                player.claimMissionReward(completedMission)
            }
        }
    }

    private val MessageReceivedEvent.action
        get() = MessageSentAction(
            Player(author.id, author.name),
            (channel as? ThreadChannel)?.parentChannel?.id ?: channel.id,
            message.contentDisplay,
            message.referencedMessage != null,
            message.attachments.any { it.isImage },
            (channel as? VoiceChannel)?.members?.size ?: 0
        )
}
