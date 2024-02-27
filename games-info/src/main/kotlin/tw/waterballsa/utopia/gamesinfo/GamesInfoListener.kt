package tw.waterballsa.utopia.gamesinfo

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

private const val GAMES_INFO_COMMAND_NAME = "games"


@Component
class GamesInfoListener : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(GAMES_INFO_COMMAND_NAME, "The mini games introduction.")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != GAMES_INFO_COMMAND_NAME) {
                return
            }
            reply("").addEmbeds(Embed {
                description = "</dice:>"
            }).queue()
        }
    }
}

private val diceGame = Embed {
    title = "dice"
}

private val guessNumberGame = Embed {
    title = "guess number"
}

private val rockPaperScissorsGame = Embed {
    title = "rock paper scissors"
}

private val rouletteGame = Embed {
    title = "roulette"
}

private val guess1a2bGame = Embed {
    title = "1a2b"
}
