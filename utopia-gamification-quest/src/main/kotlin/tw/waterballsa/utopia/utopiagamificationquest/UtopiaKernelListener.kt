package tw.waterballsa.utopia.utopiagamificationquest

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

const val UTOPIA_COMMAND_NAME = "utopia"

@Component
class UtopiaGamificationQuestListener : UtopiaListener() {

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash("utopia", "utopia command")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (!isValidCommand()) {
                return
            }
            

        }
    }

    private fun SlashCommandInteractionEvent.isValidCommand(): Boolean {
        return fullCommandName == UTOPIA_COMMAND_NAME
    }

}
