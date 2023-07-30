package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.unlockAcademyQuest
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerAcceptQuestService

const val UTOPIA_COMMAND_NAME = "utopia"

@Component
class SlashCommandListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val quests: Quests,
    private val playerAcceptQuestService: PlayerAcceptQuestService,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun commands(): List<CommandData> = listOf(Commands.slash(UTOPIA_COMMAND_NAME, "utopia command"))

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != UTOPIA_COMMAND_NAME) {
                return
            }

            val player = user.toPlayer() ?: return

            val request = PlayerAcceptQuestService.Request(player, quests.unlockAcademyQuest)

            val presenter = object : PlayerAcceptQuestService.Presenter {
                override fun presentPlayerHasAcquiredMission() {
                    replyEphemerally("已獲得新手任務，無法再次獲得。")
                }

                override fun presentPlayerAcquiresMission(mission: Mission) {
                    mission.publishToUser(user).queue()
                    replyEphemerally("已經接取第一個任務，去私訊查看任務內容。")
                }
            }

            playerAcceptQuestService.execute(request, presenter)
        }
    }
}
