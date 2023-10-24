package tw.waterballsa.utopia.utopiagamification.achievement.framework.listener

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamification.achievement.application.usecase.ProgressAchievementUseCase
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.enums.DiscordRole
import tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.presenter.ProgressAchievementPresenter


@Component
class AchievementListener(
        private val wsaGuild: Guild,
        private val discordRole: DiscordRole,
        private val progressAchievementUseCase: ProgressAchievementUseCase
) : UtopiaListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) return

            val presenter = ProgressAchievementPresenter()
            val request = ProgressAchievementUseCase.Request(author.id, TEXT_MESSAGE, message.contentDisplay)

            progressAchievementUseCase.execute(request, presenter)

            presenter.addPlayerRoles(author)

            if (presenter.isAchievementAchieved()) {
                val achievementAchievedNotification = presenter.toAchievementAchievedNotification()
                channel.sendMessage(achievementAchievedNotification).queue()
            }
        }
    }

    private fun ProgressAchievementPresenter.addPlayerRoles(player: User) {
        viewModels.map { discordRole.getRoleId(it.roleType) }
                .mapNotNull { wsaGuild.getRoleById(it) }
                .forEach { wsaGuild.addRoleToMember(player, it).queue() }
    }

}
