package tw.waterballsa.utopia.russianroulette

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import java.util.concurrent.TimeUnit

@Component
class RussianRouletteListener : UtopiaListener() {
    private val playerIdToGame = hashMapOf<String, RouletteGame>()
    override fun commands(): List<CommandData> = listOf(
        Commands.slash("roulette", "Start the game.")
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != "roulette") {
                return
            }

            val player = member!!
            playerIdToGame[player.id] = RouletteGame()

            reply("${player.asMention}，俄羅斯輪盤開始")
                .addActionRow(Button.primary("trigger", "Shoot"))
                .timeout(1, TimeUnit.MINUTES)
                .queue()
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if ("trigger" != button.id) {
                return
            }
            val player = member!!
            val rouletteGame = playerIdToGame[player.id] ?: return

            handlePlayerShoot(rouletteGame, player)
            handleBotShoot(rouletteGame, player)
        }
    }

    private fun ButtonInteractionEvent.handlePlayerShoot(rouletteGame: RouletteGame, player: Member) {
        if (rouletteGame.pullTrigger()) {
            reply("你已中彈，遊戲結束").queue()
            playerIdToGame.remove(player.id)
            return
        }
        channel.sendMessage("你成功躲過一輪，輪到我開槍").queue()
    }

    private fun ButtonInteractionEvent.handleBotShoot(rouletteGame: RouletteGame, player: Member) {
        if (rouletteGame.pullTrigger()) {
            reply("我已中彈，遊戲結束").queue()
            playerIdToGame.remove(player.id)
            return
        }
        reply("我成功躲過一輪，輪到你開槍").queue()
    }
}
