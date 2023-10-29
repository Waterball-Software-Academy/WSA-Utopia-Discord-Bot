package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.State.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.AssignedQuestException
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.QuestIds.Companion.quizQuestId
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.QuestIds.Companion.unlockAcademyQuestId
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.AssignPlayerQuestPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.AssignPlayerQuestUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

const val UTOPIA_COMMAND_NAME = "utopia"
const val FIRST_QUEST_COMMAND_NAME = "first-quest"
const val REVIEW_COMMAND_NAME = "re-render"

@Component
class SlashCommandListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val assignPlayerQuestUsecase: AssignPlayerQuestUsecase,
    private val missionRepository: MissionRepository
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(UTOPIA_COMMAND_NAME, "utopia command")
            .addSubcommands(
                SubcommandData(FIRST_QUEST_COMMAND_NAME, "get first quest"),
                SubcommandData(REVIEW_COMMAND_NAME, "re-render in_progress/completed quest"),
            )
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {

            if (name != UTOPIA_COMMAND_NAME) {
                return
            }

            deferReply(true).queue()

            when (subcommandName) {
                FIRST_QUEST_COMMAND_NAME -> handleFirstQuestCommand()
                REVIEW_COMMAND_NAME -> handleReviewCommand()
            }
        }
    }

    private fun SlashCommandInteractionEvent.handleFirstQuestCommand() {
        //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
        val player = user.toPlayer() ?: return

        val request = AssignPlayerQuestUsecase.Request(user.id, unlockAcademyQuestId)
        val presenter = AssignPlayerQuestPresenter()

        try {
            assignPlayerQuestUsecase.execute(request, presenter)

            val viewModel = presenter.viewModel ?: return

            hook.editOriginal(viewModel.assignQuestMessage).queue()
            viewModel.publishToUser(user)

        } catch (e: AssignedQuestException) {
            hook.editOriginal("已獲得新手任務，無法再次獲得！").queue()
        }
    }


    private fun SlashCommandInteractionEvent.handleReviewCommand() {
        var result = "執行結束"
        val mission = missionRepository.findAllByPlayerId(user.id).last()

        with(mission) {
            if (state == COMPLETED) {
                user.claimMissionRewardPresenter.presentClaimMissionReward(mission)
            }

            if (state == IN_PROGRESS) {
                result = "執行結束，已獲得上個任務的獎勵"
                val presenter = AssignPlayerQuestPresenter()
                presenter.presentMission(mission)
                presenter.viewModel?.publishToUser(user)
            }

            if (state == CLAIMED) {
                result = if (quest.id == quizQuestId) {
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
        }

        hook.editOriginal(result).queue()
    }
}
