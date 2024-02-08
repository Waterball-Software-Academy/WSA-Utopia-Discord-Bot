package tw.waterballsa.utopia.rollthedice

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.MessageEmbed
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


        reply("${player.asMention} 你的賭注是 $${findBet(player.id)} 🪙，開始擲骰子了喔喔喔喔喔").queue {
            diceGame.rollTheDice()

            val gameMessageId = this.messageChannel.latestMessageId
            Thread.sleep(2000)
            channel.editMessageById(
                gameMessageId,
                "${player.asMention} 骰到了 ${diceGame.getPlayerDice()[0]} 和 ${diceGame.getPlayerDice()[1]}"
            ).queue()
            Thread.sleep(2000)
            channel.editMessageById(
                gameMessageId,
                "${player.asMention} 你的對手骰到了 ${diceGame.getComputerDice()[0]} 和 ${diceGame.getComputerDice()[1]}"
            ).queue()
            Thread.sleep(2000)
            channel.editMessageById(
                gameMessageId,
                player.asMention
            ).queue()
            channel.editMessageEmbedsById(
                gameMessageId,
                endGame(diceGame, miniGamePlayer)
            ).queue()
        }
    }

    private fun endGame(diceGame: DiceGame, miniGamePlayer: MiniGamePlayer): MessageEmbed {
        val playerDice = diceGame.getPlayerDice()
        val computerDice = diceGame.getComputerDice()
        val result = diceGame.gameResult()
        val bounty = diceGame.calculateBounty(findBet(miniGamePlayer.id))
        val embedMessage =
            Embed {
                title = "遊戲結果"
                description = result
                color = 14712612
                field {
                    name = "你的骰子 🎲"
                    value = "${playerDice[0]} 和 ${playerDice[1]}"
                    inline = true
                }
                field {
                    name = "電腦的骰子 🎲"
                    value = "${computerDice[0]} 和 ${computerDice[1]}"
                }
                field {
                    name = "賞金結果 🪙"
                    value = "$bounty"
                    inline = false
                }
            }

        unRegisterGame(miniGamePlayer.id)
        gameOver(miniGamePlayer.id, bounty)

        return embedMessage
    }
}

private val SlashCommandInteractionEvent.player
    get() = member!!
