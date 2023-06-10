package tw.waterballsa.utopia.guessnum1a2b

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.guessnum1a2b.domain.*
import tw.waterballsa.utopia.jda.UtopiaListener
import java.util.concurrent.TimeUnit

val logger = KotlinLogging.logger {}
const val GUESS_NUM_1A2B_COMMAND_NAME = "1a2b"

@Component
class GuessNum1A2BListener(private val roomRepository: RoomRepository) : UtopiaListener() {
    private val gameManager = GameManager()

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(GUESS_NUM_1A2B_COMMAND_NAME, "Start a new guess num game.")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            when {
                !isValidCommandName() -> return
                channel !is TextChannel -> {
                    reply("無法在這個頻道建立遊戲").setEphemeral(true).queue()
                    return
                }
            }

            if (gameManager.isAvailableGame(member?.id!!)) {
                reply("你正在遊戲中").setEphemeral(true).queue()
            } else {
                val room = createRoom()
                val gameId = GuessNum1A2B.Id(member?.id!!, room.id)
                val newGame = gameManager.register(gameId)
                val events = newGame.startGame()

                // 建
                val roomModel = Room(gameId.roomId, gameId.playerId, newGame.answer)

                // 存
                roomRepository.save(roomModel)

                room.handleEvents(events)
            }
        }
    }

    private fun SlashCommandInteractionEvent.isValidCommandName(): Boolean {
        return fullCommandName == GUESS_NUM_1A2B_COMMAND_NAME
    }

    private fun SlashCommandInteractionEvent.createRoom(): ThreadChannel {
        return reply("1A2B遊戲開始! 將於5分鐘後關閉，${member?.asMention} 遊玩愉快~").complete()
            .retrieveOriginal().complete()
            .createThreadChannel("${member?.effectiveName}'s room").complete()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (event.author.isBot) {
                return
            }

            val room = getRoom() ?: return
            val gameId = GuessNum1A2B.Id(member?.id!!, room.id)
            val game = gameManager.find(gameId)
            game?.let {
                val events = game.guess(message.contentDisplay)
                room.handleEvents(events)

                // 查
                val roomModel = roomRepository.findById(gameId.roomId).get()
                // 改
                roomModel.guessRecords.add(message.contentDisplay)
                // 存
                roomRepository.save(roomModel)
            }
        }
    }

    private fun MessageReceivedEvent.getRoom(): ThreadChannel? {
        return if (channel is ThreadChannel) channel.asThreadChannel() else null
    }

    private fun ThreadChannel.handleEvents(events: List<Event>) {
        events.forEach { event ->
            when (event) {
                is GameStartedEvent -> handleGameStartedEvent(event)
                is AnsweredEvent -> handleAnsweredEvent(event)
                is GameOverEvent -> handleGameOverEvent(event)
            }
        }
    }

    private fun ThreadChannel.handleGameStartedEvent(event: GameStartedEvent) {
        sendMessage("已超過5分鐘!").queueAfter(5, TimeUnit.MINUTES) {
            gameManager.unregister(event.gameId)
            closeRoom()
        }
    }

    private fun ThreadChannel.handleAnsweredEvent(event: AnsweredEvent) {
        sendMessage(event.answer).queue()
    }

    private fun ThreadChannel.handleGameOverEvent(event: GameOverEvent) {
        sendMessage("恭喜猜對，遊戲結束!!").complete()
        gameManager.unregister(event.gameId)
        closeRoom()

        // 查
        val roomModel = roomRepository.findById(event.gameId.roomId).get()
        // 改
        roomModel.isVictory = true
        // 存
        roomRepository.save(roomModel)
    }

    private fun ThreadChannel.closeRoom() {
        sendMessage("遊戲房間將於10秒後關閉").queue()
        retrieveParentMessage().queueAfter(10, TimeUnit.SECONDS) { startMessage ->
            startMessage.delete().queue()
            delete().queue()
        }
    }
}
