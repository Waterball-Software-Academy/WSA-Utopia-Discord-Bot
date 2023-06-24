package tw.waterballsa.utopia.utopiagamificationquest

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands.slash
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.unlockAcademyQuest
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository.Query
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository

const val UTOPIA_COMMAND_NAME = "utopia"
const val BUTTON_QUEST_TAG = "quest"

@Component
class UtopiaGamificationQuestListener(
        private val missionRepository: MissionRepository,
        private val playerRepository: PlayerRepository,
        private val quests: Quests,
) : UtopiaListener() {

    private val log = KotlinLogging.logger {}

    override fun commands(): List<CommandData> = listOf(slash(UTOPIA_COMMAND_NAME, "utopia command"))

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != UTOPIA_COMMAND_NAME) {
                return
            }

            val userPrivateChannel = user.openPrivateChannel().complete() ?: return

            val player = user.toPlayer()
            val firstQuest = quests.unlockAcademyQuest
            val mission = player.acceptQuest(firstQuest)

            userPrivateChannel.publishMission(mission).queue {
                reply("已經接取第一個任務，去私訊查看任務內容").setEphemeral(true).queue()
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, playerId, questTitle) = button.id?.split("-") ?: return

            if (buttonTag != BUTTON_QUEST_TAG) {
                return
            }

            val userPrivateChannel = user.openPrivateChannel().complete() ?: return
            val mission = missionRepository.findMission(Query(playerId, true, questTitle)) ?: return

            mission.rewardPlayer()
            missionRepository.removeMission(mission)

            publishPlayerExpNotification(mission).complete()

            mission.nextMission()?.let {
                userPrivateChannel.publishMission(it).queue()
            }
        }
    }

    private fun ButtonInteractionEvent.publishPlayerExpNotification(mission: Mission): ReplyCallbackAction =
            with(mission) {
                reply("""
                    ${player.name} 已獲得 ${quest.reward.exp} exp!!
                    目前等級：${player.level}
                    目前經驗值：${player.exp}
                    """.trimIndent())
            }

    private fun Mission.nextMission(): Mission? = quest.nextQuest?.let { player.acceptQuest(it) }

    private fun User.toPlayer() = playerRepository.findPlayerById(id) ?: playerRepository.savePlayer(Player(id, name))

    private fun Player.acceptQuest(quest: Quest): Mission = missionRepository.saveMission(Mission(this, quest))

    private fun PrivateChannel.publishMission(mission: Mission): MessageCreateAction =
            with(mission) {
                sendMessageEmbeds(Embed {
                    title = quest.title
                    description = quest.description
                })
            }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            val player = user ?: return
            val action = action ?: return
            val incompletedMissions = missionRepository.findIncompletedMissionsByPlayerId(player.id).ifEmpty { return }
            player.fulfillMissions(action, incompletedMissions)
        }
    }

    private val MessageReactionAddEvent.action get() = user?.let { MessageReactionAction(Player(it.id, it.name), messageId, emoji.name) }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) {
                return
            }
            val player = author
            val incompletedMissions = missionRepository.findIncompletedMissionsByPlayerId(player.id).ifEmpty { return }
            player.fulfillMissions(action, incompletedMissions)
        }
    }

    private val MessageReceivedEvent.action
        get() = MessageSentAction(
                Player(author.id, author.name),
                channel.id,
                message.contentDisplay,
                message.referencedMessage != null,
                message.attachments.any { it.isImage },
                (channel as? VoiceChannel)?.members?.size ?: 0
        )

    private fun User.fulfillMissions(action: Action, missions: Collection<Mission>) {
        missions.filter { mission -> mission.match(action) }
                .onEach { mission -> mission.carryOut(action) }
                .filter { mission -> mission.isCompleted() }
                .onEach { mission -> missionRepository.saveMission(mission) }
                .forEach { claimMissionReward(it) }
    }

    private fun User.claimMissionReward(mission: Mission) {
        openPrivateChannel().queue {
            it.publishReward(mission)
        }
    }

    private fun PrivateChannel.publishReward(mission: Mission) {
        with(mission) {
            sendMessage(quest.reward.respond)
                    .addActionRow(rewardButton)
                    .queue()
        }
    }

    private val Mission.rewardButton: Button get() = button("$BUTTON_QUEST_TAG-${player.id}-${quest.title}", "領取獎勵")

}