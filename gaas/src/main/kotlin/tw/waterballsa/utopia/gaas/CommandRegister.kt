package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.jda.listener

internal const val OPTION_EVENT_DATE_YEAR = "event-date-year"
internal const val OPTION_EVENT_DATE_MONTH = "event-date-month"
internal const val OPTION_EVENT_DATE_DAY = "event-date-day"
internal const val OPTION_MEMBER = "gaas-member"

fun registerCommands() = listener {
    command {
        Commands.slash("gaas", "GaaS Command")
            .addSubcommands(
                SubcommandData("stats-avg-and-max", "Get Avg And Max Participants Number at Specific Date")
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_YEAR, "Year", true)
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_MONTH, "Month", true)
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_DAY, "Day", true)
            )
            .addSubcommands(
                SubcommandData("watch", "Add specific member to watchlist")
                    .addOption(OptionType.USER, OPTION_MEMBER, "GaaS Member", true)
            )
    }
}
