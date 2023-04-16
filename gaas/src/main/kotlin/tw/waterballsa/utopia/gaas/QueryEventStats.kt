package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntWithValidation
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt
import tw.waterballsa.utopia.jda.listener
import java.io.File
import kotlin.io.path.Path

private const val OPTION_EVENT_DATE_YEAR = "event-date-year"
private const val OPTION_EVENT_DATE_MONTH = "event-date-month"
private const val OPTION_EVENT_DATE_DAY = "event-date-day"
private const val DATABASE_DIRECTORY = "data/gaas/participation-stats"

fun getMaxAndAvgParticipantsAtSpecificDate(wsaDiscordProperties: WsaDiscordProperties) = listener {
    command {
        Commands.slash("gaas", "Query GaaS Event Stats")
            .addSubcommands(
                SubcommandData("stats-avg-and-max", "Get Avg And Max Participants Number at Specific Date")
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_YEAR, "Year", true)
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_MONTH, "Month", true)
                    .addOption(OptionType.INTEGER, OPTION_EVENT_DATE_DAY, "Day", true)
            )
    }

    on<SlashCommandInteractionEvent> {
        val alphaRoleId = wsaDiscordProperties.wsaAlphaRoleId
        val commandUser = member!!

        when {
            interaction.fullCommandName != "gaas stats-avg-and-max" -> return@on
            !commandUser.isAlphaMember(alphaRoleId) -> {
                replayEphemerally("權限不足")
                return@on
            }
        }

        val year = getOptionAsPositiveInt(OPTION_EVENT_DATE_YEAR)
        val month = getOptionAsIntWithValidation(OPTION_EVENT_DATE_MONTH, "1 ~ 12") { it in 1..12 }
        val day = getOptionAsIntWithValidation(OPTION_EVENT_DATE_DAY, "1 ~ 31") { it in 1..31 }
        val date = combineAsDate(year, month, day)

        takeUnless { validateDateFormat(date) }
            ?.run {
                replayEphemerally("日期格式不合法")
                return@on
            }

        val eventStatsFile =
            getEventStatsFile(date)
                ?: run {
                    replayEphemerally("查無指定日期的資料")
                    return@on
                }

        eventStatsFile.useLines { lines ->
            val avgAndMax = lines
                .filter { line -> line.contains("Avg:") || line.contains("Max:") }
                .joinToString(System.lineSeparator())

            replayEphemerally(avgAndMax)
        }
    }
}

private fun validateDateFormat(date: String) = date matches Regex("""^\d{4}-\d{2}-\d{2}$""")

private fun combineAsDate(year: Int?, month: Int?, day: Int?): String =
    buildString {
        append(year)
        append("-")
        append(appendZeroPrefixIfNeeded(month))
        append("-")
        append(appendZeroPrefixIfNeeded(day))
    }

private fun appendZeroPrefixIfNeeded(num: Int?) = if (num!! < 10) "0$num" else "$num"

private fun getEventStatsFile(date: String): File? =
    Path(DATABASE_DIRECTORY)
        .toFile()
        .walkTopDown()
        .firstOrNull { it.name.contains(date) }

private fun Member.isAlphaMember(alphaRoleId: String): Boolean =
    alphaRoleId in roles.mapNotNull { it.id }

private fun SlashCommandInteractionEvent.replayEphemerally(message: String) =
    reply(message).setEphemeral(true).queue()
