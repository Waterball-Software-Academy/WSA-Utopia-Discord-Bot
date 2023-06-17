package tw.waterballsa.utopia.utopiagamificationquest

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.repository.DummyDataBase
import tw.waterballsa.utopia.utopiagamificationquest.domain.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.*

const val UTOPIA_COMMAND_NAME = "utopia"
const val BUTTON_QUEST_TAG = "quest"

@Component
class UtopiaGamificationQuestListener(
        @Autowired private val repository: DummyDataBase,
        @Autowired private val quests: Quests
) : UtopiaListener() {

    private val log = KotlinLogging.logger {}

    override fun commands(): List<CommandData> {
        return listOf(Commands.slash("utopia", "utopia command"))
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != UTOPIA_COMMAND_NAME) {
                return
            }

            val userPrivateChannel = user.openPrivateChannel().complete() ?: return
            val firstQuest = quests.participateInDiscussionQuest

            val mission = saveMission(Player(user.id, user.name), firstQuest)
            userPrivateChannel.publishMission(mission).queue {
                reply("已經接取第一個任務，去私訊查看任務內容").setEphemeral(true).queue()
            }
        }
    }

    private fun saveMission(newPlayer: Player, quest: Quest): Mission {
        val player = repository.savePlayer(newPlayer)
        val mission = Mission(player, quest)
        return repository.saveMission(mission)
    }

    private fun PrivateChannel.publishMission(mission: Mission): MessageCreateAction {
        val quest = mission.quest

        return sendMessageEmbeds(Embed {
            title = quest.title
            description = quest.description
        })
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {

            val user = user ?: return
            val uncompletedMissions = repository.findUncompletedMissionsByPlayerId(user.id).ifEmpty { return }
            val action = toAction()

            uncompletedMissions.filter { it.match(action) }
                    .onEach { it.carryOut(action) }
                    .filter { it.isCompleted() }
                    .onEach {
                        repository.saveMission(it)
                        notifyPlayerToClaimMissionReward(it, user)
                    }
        }
    }

    private fun MessageReactionAddEvent.toAction(): MessageReactionAction = MessageReactionAction(messageId, emoji.name)

    private fun notifyPlayerToClaimMissionReward(mission: Mission, user: User) {
        user.openPrivateChannel().queue {
            it.sendClaimMissionRewardMessage(mission)
        }
    }

    private fun PrivateChannel.sendClaimMissionRewardMessage(mission: Mission) {
        with(mission) {
            sendMessage(quest.reward.respond)
                    .addActionRow(
                            button(getButtonId(quest.title, player.id), "領取獎勵")
                    ).queue()
        }
    }

    private fun getButtonId(questTitle: String, playerId: String): String = "$BUTTON_QUEST_TAG-$playerId-$questTitle"

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (author.isBot) {
                return
            }

            val playerId = author.id
            val uncompletedMissions = repository.findUncompletedMissionsByPlayerId(playerId).ifEmpty { return }
            val action = toAction()

            uncompletedMissions.filter { it.match(action) }
                    .onEach { it.carryOut(action) }
                    .filter { it.isCompleted() }
                    .onEach {
                        repository.saveMission(it)
                        notifyPlayerToClaimMissionReward(it, author)
                    }
        }
    }

    private fun MessageReceivedEvent.toAction(): MessageSentAction = MessageSentAction(channel.id, message.contentDisplay)

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, playerId, questTitle) = button.id?.split("-") ?: return

            if (buttonTag != BUTTON_QUEST_TAG) {
                return
            }

            val userPrivateChannel = user.openPrivateChannel().complete() ?: return
            val mission = repository
                    .findMissionsByPlayerId(playerId)
                    .find { it.quest.title == questTitle } ?: return

            if (!mission.isCompleted()) {
                return
            }

            mission.givePlayerExp()
            repository.removeMission(mission)
            replyGetRewardMessage(mission).complete()

            mission.quest.nextQuest?.let { nextQuest ->
                val nextMission = saveMission(Player(playerId, user.name), nextQuest)
                userPrivateChannel.publishMission(nextMission).queue()
            }
        }
    }

    private fun ButtonInteractionEvent.replyGetRewardMessage(mission: Mission): ReplyCallbackAction {
        with(mission) {
            return reply(
                    """
                    ${player.name} 已獲得 ${quest.reward.exp} exp!!
                    目前等級：${player.level}
                    目前經驗值：${player.exp}
                    """.trimIndent())
        }

    }
}
