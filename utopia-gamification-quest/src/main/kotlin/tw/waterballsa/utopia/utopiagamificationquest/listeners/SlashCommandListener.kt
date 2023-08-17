package tw.waterballsa.utopia.utopiagamificationquest.listeners

import kotlinx.coroutines.handleCoroutineException
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.State
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.unlockAcademyQuest
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.ClaimMissionRewardService
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerAcceptQuestService

const val UTOPIA_COMMAND_NAME = "utopia"
const val FIRST_QUEST_COMMAND_NAME = "first-quest"
const val REVIEW_COMMAND_NAME = "review"

@Component
class SlashCommandListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val quests: Quests,
    private val playerAcceptQuestService: PlayerAcceptQuestService,
    private val missionRepository: MissionRepository
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(UTOPIA_COMMAND_NAME, "utopia command")
            .addSubcommands(
                SubcommandData(FIRST_QUEST_COMMAND_NAME, "get first quest"),
                SubcommandData(REVIEW_COMMAND_NAME, "re-render quest")
            )
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {

            if (name != UTOPIA_COMMAND_NAME) {
                return
            }

            deferReply().setEphemeral(true).queue()

            when (subcommandName) {
                FIRST_QUEST_COMMAND_NAME -> handleFirstQuestCommand()
                REVIEW_COMMAND_NAME -> handleReviewCommand()
            }
        }
    }

    private fun SlashCommandInteractionEvent.handleFirstQuestCommand() {
        val player = user.toPlayer() ?: return

        val request = PlayerAcceptQuestService.Request(player, quests.unlockAcademyQuest)

        val presenter = object : PlayerAcceptQuestService.Presenter {
            override fun presentPlayerHasAcquiredMission() {
                hook.editOriginal("已獲得新手任務，無法再次獲得。").queue()
            }

            override fun presentPlayerAcquiresMission(mission: Mission) {
                mission.publishToUser(user)
                hook.editOriginal("已經接取第一個任務，去私訊查看任務內容。").queue()
            }
        }

        playerAcceptQuestService.execute(request, presenter)
    }

    private fun SlashCommandInteractionEvent.handleReviewCommand() {
        var result = "執行結束"
        val mission = missionRepository.findAllByPlayerId(user.id).last()

        if (mission.state == State.COMPLETED) {
            user.claimMissionRewardPresenter.presentClaimMissionReward(mission)
        }

        if (mission.state == State.IN_PROGRESS) {
            result += "，已獲得上個任務的獎勵"
            mission.publishToUser(user)
        }

        if (mission.state == State.CLAIMED) {
            result = if (mission.quest.id == 10) {
                "你已完成全部的新手任務！"
            } else {
                jda.retrieveUserById("620215716993433612").queue {
                    it.openPrivateChannel().queue { channel ->
                        channel.sendMessage("${user.effectiveName} (${user.id}) : message -> mission 發生資料異常")
                            .queue()
                    }
                }
                "資料異常，已通知 tech 進行維修"
            }
        }

        hook.editOriginal(result).queue()
    }
}
