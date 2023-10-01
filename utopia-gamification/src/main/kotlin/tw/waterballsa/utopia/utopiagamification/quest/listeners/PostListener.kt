package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.PostAction
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

@Component
class PostListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildAuditLogEntryCreate(event: GuildAuditLogEntryCreateEvent) {
        with(event) {
            val user = user ?: return
            val channel = guild.getThreadChannelById(entry.targetId)?.parentChannel ?: return
            val player = user.toPlayer() ?: return

            val action = PostAction(
                player,
                channel.id
            )

            playerFulfillMissionsUsecase.execute(action, user.claimMissionRewardPresenter)
        }
    }

    private val GuildAuditLogEntryCreateEvent.user get() = entry.user ?: jda.retrieveUserById(entry.userId).complete()
}
