package tw.waterballsa.utopia.usageinformation.richest

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

private const val RICHEST_COMMAND_NAME = "richest"

@Component
class RichestListener() : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(RICHEST_COMMAND_NAME, "richest rank")
        )
    }
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != RICHEST_COMMAND_NAME){
                return
            }
        }
    }

}
