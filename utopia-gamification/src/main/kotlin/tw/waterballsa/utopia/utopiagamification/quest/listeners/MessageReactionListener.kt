package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.PlayerFulfillMissionPresenter
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
            //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
            val user = user ?: return
            val player = user.toPlayer() ?: return

            val action = MessageReactionAction(
                user.id,
                messageId,
                emoji.name
            )

            val presenter = PlayerFulfillMissionPresenter()

            playerFulfillMissionsUsecase.execute(action, presenter)

            presenter.viewModel?.publishToUser(user)
        }
    }
}
