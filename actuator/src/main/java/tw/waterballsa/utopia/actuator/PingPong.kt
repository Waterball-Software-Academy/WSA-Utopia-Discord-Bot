package tw.waterballsa.utopia.actuator

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

/**
 * @author timm
 */
@Component
class PingPong() : UtopiaListener (){
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

    }
}
