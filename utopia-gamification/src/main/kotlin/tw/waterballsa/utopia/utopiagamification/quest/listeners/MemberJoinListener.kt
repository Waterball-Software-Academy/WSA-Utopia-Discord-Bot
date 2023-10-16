package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerAcceptQuestUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound

private const val unlockAcademyQuestId = 1

@Component
class MemberJoinListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val questRepository: QuestRepository,
    private val playerAcceptQuestUsecase: PlayerAcceptQuestUsecase,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        with(event) {

            val player = user.toPlayer() ?: return
            val unlockAcademyQuest = questRepository.findById(unlockAcademyQuestId)
                ?: throw notFound(Quest::class)
                    .id(unlockAcademyQuestId)
                    .message("assign new member first quest")
                    .build()

            val request = PlayerAcceptQuestUsecase.Request(player, unlockAcademyQuest)

            val presenter = object : PlayerAcceptQuestUsecase.Presenter {
                override fun presentPlayerHasAcquiredMission() {
                    sendMessageToUserPrivateChannel("已獲得新手任務，無法再次獲得！")
                }

                override fun presentPlayerAcquiresMission(mission: Mission) {
                    mission.publishToUser(user)
                }
            }

            playerAcceptQuestUsecase.execute(request, presenter)
        }
    }

    private fun GuildMemberJoinEvent.sendMessageToUserPrivateChannel(message: String) =
        user.openPrivateChannel().queue { it.sendMessage(message).queue() }
}
