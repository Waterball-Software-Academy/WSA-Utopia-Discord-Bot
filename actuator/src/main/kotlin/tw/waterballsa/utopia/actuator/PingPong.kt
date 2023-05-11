package tw.waterballsa.utopia.actuator

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

/**
 * @author timm
 */

private const val PING_COMMAND_NAME = "ping"

@Component
class PingPong() : UtopiaListener() {

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(PING_COMMAND_NAME, "sends pong")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (!fullCommandName.isPingCommand()) {
                return
            }
            reply("pong").setEphemeral(true).queue()
        }
    }

    private fun String.isPingCommand(): Boolean {
        return PING_COMMAND_NAME == this
    }
}
