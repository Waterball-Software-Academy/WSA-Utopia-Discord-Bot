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
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.minigames.UtopiaListenerImpl
import tw.waterballsa.utopia.jda.domains.EventPublisher
import java.util.concurrent.TimeUnit

val logger = KotlinLogging.logger {}
const val GUESS_NUM_1A2B_COMMAND_NAME = "1a2b"

@Component
class GuessNum1A2BListener(
    publisher: EventPublisher,
    playerFinder: PlayerFinder
) : UtopiaListenerImpl<GuessNum1A2B>(publisher, playerFinder) {
    override val playerIdToGame = hashMapOf<String, GuessNum1A2B>()
    private val discordUserIdToMiniPlayerId = hashMapOf<String, String>()
    private val gameManager = GameManager()
    private var guessedRound = 1

    override fun getCommandName(): String {
        return GUESS_NUM_1A2B_COMMAND_NAME
    }

    override fun getCommandDescription(): String {
        return "Start a new guess num game."
    }

    override fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer) {
        if (playerIdToGame[miniGamePlayer.id] != null) {
            return
        }

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

            registerGame(miniGamePlayer.id, newGame)
            discordUserIdToMiniPlayerId[member!!.id] = miniGamePlayer.id
            room.handleEvents(events)
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
            unRegisterGame(discordUserIdToMiniPlayerId[event.gameId.playerId]!!)
            closeRoom()
        }
    }

    private fun ThreadChannel.handleAnsweredEvent(event: AnsweredEvent) {
        sendMessage(event.answer).queue {
            guessedRound += 1
        }
    }

    private fun ThreadChannel.handleGameOverEvent(event: GameOverEvent) {
        val miniGamePlayerBet = findBet(event.gameId.playerId).toInt()
        val miniGameBountyResult = when (guessedRound) {
            in 1..3 -> {
                (miniGamePlayerBet * 1.25).toInt()
            }

            in 4..6 -> {
                miniGamePlayerBet
            }

            else -> {
                miniGamePlayerBet / 2
            }
        }
        sendMessage(
            """
                恭喜猜對，遊戲結束!!
                賞金：$miniGameBountyResult
                $guessedRound
            """.trimIndent()
        ).complete()
        gameManager.unregister(event.gameId)
        unRegisterGame(discordUserIdToMiniPlayerId[event.gameId.playerId]!!)
        closeRoom()
    }

    private fun ThreadChannel.closeRoom() {
        sendMessage("遊戲房間將於10秒後關閉").queue {
            guessedRound = 1
        }
        retrieveParentMessage().queueAfter(10, TimeUnit.SECONDS) { startMessage ->
            startMessage.delete().queue()
            delete().queue()
        }
    }
}
