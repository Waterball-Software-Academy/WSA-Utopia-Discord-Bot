package tw.waterballsa.utopia.utopiagamification.achievement.framework.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamification.achievement.application.usecase.ProgressAchievementUseCase
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.enums.DiscordRole
import tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.presenter.ProgressAchievementPresenter


@Component
class AchievementListener(
    private val discordRole: DiscordRole,
    private val progressAchievementUseCase: ProgressAchievementUseCase
) : UtopiaListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) return

            val presenter = ProgressAchievementPresenter()
            val action = ProgressAchievementUseCase.Request(
                author.id,
                TEXT_MESSAGE,
                message.contentDisplay,
            )

            progressAchievementUseCase.execute(action, presenter)
            addRolesToPlayer(presenter)
            if (presenter.isViewModelsNotEmpty()) {
                channel.sendMessage(presenter.toMessage()).queue()
            }
        }
    }

    private fun MessageReceivedEvent.addRolesToPlayer(presenter: ProgressAchievementPresenter) {
        presenter.toRoleIds().forEach {
            guild.addRoleToMember(author, guild.getRoleById(it)!!).complete()
        }
    }

    private fun ProgressAchievementPresenter.toRoleIds(): List<String> =
        progressAchievementViewModels.map { discordRole.getRoleId(it.roleType) }
}
