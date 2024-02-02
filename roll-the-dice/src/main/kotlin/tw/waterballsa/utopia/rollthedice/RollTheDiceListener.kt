package tw.waterballsa.utopia.rollthedice

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.minigames.UtopiaListenerImpl
import tw.waterballsa.utopia.rollthedice.domain.DiceGame


private const val ROLL_THE_DICE_COMMAND = "dice"

@Component
class RollTheDiceListener(
    publisher: EventPublisher,
    playerFinder: PlayerFinder
) : UtopiaListenerImpl<DiceGame>(publisher, playerFinder) {
    override val playerIdToGame = hashMapOf<String, DiceGame>()
    private val memberIdToMiniGamePlayer = hashMapOf<String, MiniGamePlayer>()

    override fun getCommandName(): String {
        return ROLL_THE_DICE_COMMAND
    }

    override fun getCommandDescription(): String {
        return "骰兩顆骰子比骰子總和大小"
    }

    override fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer) {
        if (playerIdToGame[miniGamePlayer.id] != null) {
            return
        }
        registerGame(miniGamePlayer.id, DiceGame())
        val diceGame = playerIdToGame[miniGamePlayer.id]!!

        memberIdToMiniGamePlayer[player.id] = miniGamePlayer


        reply("${player.asMention} 你的賭注是 $${findBet(player.id)}，開始擲骰子了喔喔喔喔喔").queue {
            diceGame.rollTheDice()
            channel.sendMessage("${player.asMention}\n${endGame(diceGame, miniGamePlayer)}").queue()
        }
    }

    private fun endGame(diceGame: DiceGame, miniGamePlayer: MiniGamePlayer): String {
        val playerGetBounty = diceGame.calculateBounty(miniGamePlayer.bounty.toInt())
        val playerDice = diceGame.getPlayerDice()
        val computerDice = diceGame.getComputerDice()
        val result = diceGame.gameResult()
        val bounty = diceGame.calculateBounty(findBet(miniGamePlayer.id).toInt())
        val message =
            "**這局遊戲的結果是：${result}**\n你的骰子：${playerDice[0]}, ${playerDice[1]}\n" +
                    "電腦的骰子：${computerDice[0]}, ${computerDice[1]}\n賞金結果：${bounty}"

        unRegisterGame(miniGamePlayer.id)
        gameOver(miniGamePlayer.id, playerGetBounty)

        return message
    }
}

private val SlashCommandInteractionEvent.player
    get() = member!!
