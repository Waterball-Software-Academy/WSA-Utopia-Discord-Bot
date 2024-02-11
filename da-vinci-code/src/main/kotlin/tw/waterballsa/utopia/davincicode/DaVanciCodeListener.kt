package tw.waterballsa.utopia.davincicode

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.davincicode.domain.DaVanciCodeGame
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.minigames.UtopiaListenerImpl

private const val COMMAND_NAME = "guess"

@Component
class DaVanciCodeListener(
    publisher: EventPublisher,
    playerFinder: PlayerFinder
) : UtopiaListenerImpl<DaVanciCodeGame>(publisher, playerFinder) {
    override val playerIdToGame = hashMapOf<String, DaVanciCodeGame>()
    private val memberIdToMiniGamePlayer = hashMapOf<String, MiniGamePlayer>()

    override fun getCommandName(): String {
        return COMMAND_NAME
    }

    override fun getCommandDescription(): String {
        return "Start to play the Da Vanci Code game."
    }

    override fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer) {
        if (playerIdToGame[miniGamePlayer.id] != null) {
            return
        }
        registerGame(miniGamePlayer.id, DaVanciCodeGame())

        memberIdToMiniGamePlayer[player.id] = miniGamePlayer

        reply(
            "${player.asMention}，終極密碼即將開始，你的賭注是 \$${findBet(player.id)}，一共有五次機會" +
                    "，數字在 1~100 之間，請輸入你的答案"
        ).queue()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (event.author.isBot) {
                return
            }
            val miniGamePlayer = memberIdToMiniGamePlayer[player.id] ?: return
            val daVanciCodeGame = playerIdToGame[miniGamePlayer.id] ?: return
            val playerMessage = message.contentDisplay.toInt()

            handlePlayerGuessNumber(miniGamePlayer, daVanciCodeGame, playerMessage)
        }
    }

    private fun MessageReceivedEvent.handlePlayerGuessNumber(
        miniGamePlayer: MiniGamePlayer,
        daVanciCodeGame: DaVanciCodeGame,
        message: Int
    ) {
        val text = daVanciCodeGame.guessNumber(message)

        if (daVanciCodeGame.isGameOver()) {
            val playerBet = findBet(miniGamePlayer.id)
            var playerBounty = miniGamePlayer.bounty

            val playerGetBounty = daVanciCodeGame.calculateBounty(playerBet)
            playerBounty += playerGetBounty

            channel.sendMessage("${player.asMention} 遊戲結束，${text} ${playerGetBounty}。").queue{
                playerIdToGame.remove(miniGamePlayer.id)
                unRegisterGame(miniGamePlayer.id)
                gameOver(miniGamePlayer.id, playerBounty)
            }
            return
        }
        channel.sendMessage(text).queue()
    }

}

private val SlashCommandInteractionEvent.player
    get() = member!!

private val MessageReceivedEvent.player
    get() = member!!
