package tw.waterballsa.utopia.utopiagamificationquest

import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands.slash
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.BUTTON_QUEST_TAG
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.QuizButton
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.quizQuest
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.unlockAcademyQuest
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.extensions.quizButton
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.ClaimMissionRewardService
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerAcceptQuestService
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

const val UTOPIA_COMMAND_NAME = "utopia"
private val log = KotlinLogging.logger {}

@Component
class UtopiaGamificationQuestListener(
    private val playerRepository: PlayerRepository,
    private val quests: Quests,
    private val playerAcceptQuestService: PlayerAcceptQuestService,
    private val claimMissionRewardService: ClaimMissionRewardService,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService
) : UtopiaListener() {

    override fun commands(): List<CommandData> = listOf(slash(UTOPIA_COMMAND_NAME, "utopia command"))

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != UTOPIA_COMMAND_NAME) {
                return
            }

            val request = PlayerAcceptQuestService.Request(user.toPlayer(), quests.unlockAcademyQuest)

            val presenter = object : PlayerAcceptQuestService.Presenter {
                override fun presentPlayerHasAcquiredMission() {
                    reply("已獲得新手任務，無法再次獲得。").setEphemeral(true).queue()
                }

                override fun presentPlayerAcquiresMission(mission: Mission) {
                    mission.publishToUser(user).queue()
                    reply("已經接取第一個任務，去私訊查看任務內容。").setEphemeral(true).queue()
                }
            }

            playerAcceptQuestService.execute(request, presenter)
        }
    }

    private fun User.toPlayer() = playerRepository.findPlayerById(id) ?: playerRepository.savePlayer(Player(id, name))

    private fun Mission.publishToUser(user: User): MessageCreateAction =
        user.openPrivateChannel().complete().publishMission(this)

    private fun PrivateChannel.publishMission(mission: Mission): MessageCreateAction =
        with(mission) {
            val message = sendMessageEmbeds(Embed {
                title = quest.title
                description = quest.description
            })

            if (quest.title == quests.quizQuest.title) {
                message.addActionRow(
                    quizButton
                )
            }

            return message
        }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, buttonName, args) = splitButtonId("-")

            if (buttonTag != BUTTON_QUEST_TAG) {
                return
            }

            val action = ButtonInteractionAction(Player(user.id, user.name), buttonName)

            when (action.buttonName) {
                RewardButton.NAME -> handleRewardButtonInteraction(action, args[0])
                else -> {
                    playerFulfillMissionsService.execute(action) {
                        user.claimMissionReward(it)
                    }
                }
            }


        }
    }

    private fun ButtonInteractionEvent.splitButtonId(delimiters: String): Triple<String, String, Array<String>> {
        val result = button.id?.split(delimiters) ?: return Triple("", "", arrayOf())

        val tag = result.getOrElse(0) { "" }
        val buttonName = result.getOrElse(1) { "" }
        val args = if (result.size > 2) result.drop(2).toTypedArray() else arrayOf()

        return Triple(tag, buttonName, args)
    }

    private fun ButtonInteractionEvent.handleRewardButtonInteraction(
        action: ButtonInteractionAction,
        questTitle: String
    ) {

        val request = ClaimMissionRewardService.Request(action.player, questTitle)
        val presenter = object : ClaimMissionRewardService.Presenter {
            override fun presentMission(mission: Mission) {
                publishPlayerExpNotification(mission)
            }

            override fun presentNextMission(mission: Mission) {
                mission.publishToUser(user).complete()
            }
        }
        claimMissionRewardService.execute(request, presenter)
    }

    private fun ButtonInteractionEvent.publishPlayerExpNotification(mission: Mission) =
        with(mission) {
            reply(
                """
                    ${player.name} 已獲得 ${quest.reward.exp} exp!!
                    目前等級：${player.level}
                    目前經驗值：${player.exp}
                    """.trimIndent()
            ).complete()
        }

}

