package tw.waterballsa.utopia.actuator

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

/**
 * @author timm
 */
@Component
class PingPong() : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash("ping", "sends pong")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            reply("pong").setEphemeral(true).queue()
        }
    }
}
