package tw.waterballsa.utopia.russianroulette

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button.primary
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import java.util.concurrent.TimeUnit.*

//TODO:
// 1. 繼承 UtopiaListenerImpl 並覆寫方法

private const val COMMAND_NAME = "roulette"
private const val BUTTON_ID = "trigger"

@Component
class RussianRouletteListener : UtopiaListener() {
    private val playerIdToGame = hashMapOf<String, RouletteGame>()

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(COMMAND_NAME, "Start the game.")
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != COMMAND_NAME) {
                return
            }

            playerIdToGame[player.id] = RouletteGame()

            reply("${player.asMention}，俄羅斯輪盤開始")
                .addActionRow(primary(BUTTON_ID, "Shoot"))
                .timeout(1, MINUTES)
                .queue()
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if (BUTTON_ID != button.id) {
                return
            }
            val rouletteGame = playerIdToGame[player.id] ?: return

            handlePlayerShoot(rouletteGame)
            handleBotShoot(rouletteGame)
        }
    }

    private fun ButtonInteractionEvent.handlePlayerShoot(rouletteGame: RouletteGame) {
        handleShoot(rouletteGame, "你已中彈，遊戲結束")
        channel.sendMessage("你成功躲過一輪，輪到我開槍").queue()
    }

    private fun ButtonInteractionEvent.handleBotShoot(rouletteGame: RouletteGame) {
        handleShoot(rouletteGame, "我已中彈，遊戲結束")
        reply("我成功躲過一輪，輪到你開槍").queue()
    }

    private fun ButtonInteractionEvent.handleShoot(rouletteGame: RouletteGame, hint: String) {
        rouletteGame.pullTrigger()
        if (rouletteGame.isGameOver()) {
            reply(hint).queue { playerIdToGame.remove(player.id) }
            return
        }
    }
}

private val SlashCommandInteractionEvent.player
    get() = member!!

private val ButtonInteractionEvent.player
    get() = member!!
