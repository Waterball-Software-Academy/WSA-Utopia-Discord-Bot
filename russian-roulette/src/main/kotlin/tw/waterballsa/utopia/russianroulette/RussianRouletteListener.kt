package tw.waterballsa.utopia.russianroulette

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button.primary
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.minigames.UtopiaListenerImpl
import java.util.concurrent.TimeUnit.*

private const val COMMAND_NAME = "roulette"
private const val BUTTON_ID = "trigger"

@Component
class RussianRouletteListener(
    publisher: EventPublisher,
    playerFinder: PlayerFinder
) : UtopiaListenerImpl<RouletteGame>(publisher, playerFinder) {
    override val playerIdToGame = hashMapOf<String, RouletteGame>()
    private val memberIdToMiniGamePlayer = hashMapOf<String, MiniGamePlayer>()

    override fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer) {
        if (playerIdToGame[miniGamePlayer.id] != null) {
            return
        }
        registerGame(miniGamePlayer.id, RouletteGame())

        memberIdToMiniGamePlayer[player.id] = miniGamePlayer

        reply("${player.asMention}，俄羅斯輪盤開始")
            .addActionRow(primary(BUTTON_ID, "Shoot"))
            .timeout(1, MINUTES)
            .queue()
    }

    override fun getCommandName(): String {
        return COMMAND_NAME
    }

    override fun getCommandDescription(): String {
        return "Start a new biu biu biu game"
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if (BUTTON_ID != button.id) {
                return
            }

            val miniGamePlayer = memberIdToMiniGamePlayer[player.id] ?: return
            val rouletteGame = playerIdToGame[miniGamePlayer.id] ?: return

            reply("biubiubiu").queue{
                handlePlayerShoot(rouletteGame, miniGamePlayer)         }
        }
    }

    private fun ButtonInteractionEvent.handlePlayerShoot(rouletteGame: RouletteGame, miniGamePlayer: MiniGamePlayer) {
        rouletteGame.pullTrigger()
        if (rouletteGame.isGameOver()) {
            val playerBet = findBet(miniGamePlayer.id).toInt()
            val playerGetBounty = rouletteGame.calculateBounty(playerBet)

            var playerBounty = miniGamePlayer.bounty.toInt()
            playerBounty += playerGetBounty

            channel.sendMessage("你已中彈，遊戲結束，獲得 $playerGetBounty 賞金").queue {
                playerIdToGame.remove(miniGamePlayer.id)
                unRegisterGame(miniGamePlayer.id)
                gameOver(miniGamePlayer.id, playerBounty)
            }
            return
        }
        rouletteGame.survivalCount()
        channel.sendMessage("你成功躲過這一回，請繼續開槍射擊").queue()
    }
}

private val SlashCommandInteractionEvent.player
    get() = member!!

private val ButtonInteractionEvent.player
    get() = member!!
