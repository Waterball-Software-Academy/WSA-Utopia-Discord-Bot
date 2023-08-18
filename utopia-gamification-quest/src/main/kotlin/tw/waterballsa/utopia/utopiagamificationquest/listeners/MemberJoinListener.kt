package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.unlockAcademyQuest
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerAcceptQuestService

@Component
class MemberJoinListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val quests: Quests,
    private val playerAcceptQuestService: PlayerAcceptQuestService,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        with(event) {

            val player = user.toPlayer() ?: return
            val request = PlayerAcceptQuestService.Request(player, quests.unlockAcademyQuest)

            val presenter = object : PlayerAcceptQuestService.Presenter {
                override fun presentPlayerHasAcquiredMission() {
                    sendMessageToUserPrivateChannel("已獲得新手任務，無法再次獲得！")
                }

                override fun presentPlayerAcquiresMission(mission: Mission) {
                    mission.publishToUser(user)
                }
            }

            playerAcceptQuestService.execute(request, presenter)
        }
    }

    private fun GuildMemberJoinEvent.sendMessageToUserPrivateChannel(message: String) =
        user.openPrivateChannel().queue { it.sendMessage(message).queue() }
}
