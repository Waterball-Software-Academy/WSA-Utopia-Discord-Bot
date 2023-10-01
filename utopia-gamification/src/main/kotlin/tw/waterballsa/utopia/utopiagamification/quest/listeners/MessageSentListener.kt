package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

@Component
class MessageSentListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase
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

            playerFulfillMissionsUsecase.execute(action, user.claimMissionRewardPresenter)
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
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

            playerFulfillMissionsUsecase.execute(action, user.claimMissionRewardPresenter)
        }
    }
}
