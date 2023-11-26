package tw.waterballsa.utopia.coinmigration

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

const val COIN_MIGRATION = "coinmigration"

@Component
class CoinMigrationListener(
    playerRepository: PlayerRepository
) : UtopiaListener() {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(COIN_MIGRATION, "mee6 coin migration.")
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        if (event.name != COIN_MIGRATION) {
            return
        }

        val users = event.guild?.members

        users?.forEach {
            event.channel.sendMessage("coins ${it.asMention}")
        }
    }
}