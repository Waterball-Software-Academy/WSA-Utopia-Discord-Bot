package tw.waterballsa.utopia.usageinformation.economyinfo

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import tw.waterballsa.utopia.jda.UtopiaListener

private const val ECONOMY_INFO_COMMAND_NAME = "economy-info"

class EconomyInfoListener() : UtopiaListener() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
            with(event){
                if (fullCommandName != ECONOMY_INFO_COMMAND_NAME){
                    return
                }


            }

    }
}
