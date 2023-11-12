package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.PlayerFulfillMissionPresenter
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

            //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
            val user = author
            val player = user.toPlayer() ?: return

            val action = MessageSentAction(
                user.id,
                (channel as? ThreadChannel)?.parentChannel?.id ?: channel.id,
                message.contentDisplay,
                message.referencedMessage != null,
                message.attachments.any { it.isImage },
                (channel as? VoiceChannel)?.members?.size ?: 0
            )
            val presenter = PlayerFulfillMissionPresenter()

            playerFulfillMissionsUsecase.execute(action, presenter)

            presenter.viewModel?.publishToUser(user)
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        with(event) {
            if (author.isBot) {
                return
            }

            //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
            val user = author
            val player = user.toPlayer() ?: return

            val action = MessageSentAction(
                user.id,
                (channel as? ThreadChannel)?.parentChannel?.id ?: channel.id,
                message.contentDisplay,
                message.referencedMessage != null,
                message.attachments.any { it.isImage },
                (channel as? VoiceChannel)?.members?.size ?: 0
            )

            val presenter = PlayerFulfillMissionPresenter()

            playerFulfillMissionsUsecase.execute(action, presenter)

            presenter.viewModel?.publishToUser(user)
        }
    }
}
