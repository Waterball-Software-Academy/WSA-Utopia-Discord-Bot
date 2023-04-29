package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntWithValidation
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt
import tw.waterballsa.utopia.jda.listener
import java.io.File
import kotlin.io.path.Path

private const val DATABASE_DIRECTORY = "data/gaas/participation-stats"

fun getMaxAndAvgParticipantsAtSpecificDate(wsaDiscordProperties: WsaDiscordProperties) = listener {

    on<SlashCommandInteractionEvent> {
        val alphaRoleId = wsaDiscordProperties.wsaAlphaRoleId
        val commandUser = member!!

        when {
            interaction.fullCommandName != "gaas stats-avg-and-max" -> return@on
            !commandUser.isAlphaMember(alphaRoleId) -> {
                replyEphemerally("權限不足")
                return@on
            }
        }

        val year = getOptionAsPositiveInt(OPTION_EVENT_DATE_YEAR)
        val month = getOptionAsIntWithValidation(OPTION_EVENT_DATE_MONTH, "1 ~ 12") { it in 1..12 }
        val day = getOptionAsIntWithValidation(OPTION_EVENT_DATE_DAY, "1 ~ 31") { it in 1..31 }
        val date = combineAsDate(year, month, day)

        takeUnless { validateDateFormat(date) }
            ?.run {
                replyEphemerally("日期格式不合法")
                return@on
            }

        val eventStatsFile =
            getEventStatsFile(date)
                ?: run {
                    replyEphemerally("查無指定日期的資料")
                    return@on
                }

        eventStatsFile.useLines { lines ->
            val avgAndMax = lines
                .filter { line -> line.contains("Avg:") || line.contains("Max:") }
                .joinToString(System.lineSeparator())

            replyEphemerally(avgAndMax)
        }
    }
}

private fun validateDateFormat(date: String) = date matches Regex("""^\d{4}-\d{2}-\d{2}$""")

private fun combineAsDate(year: Int?, month: Int?, day: Int?): String =
    buildString {
        append("$year-${appendZeroPrefixIfNeeded(month)}-${appendZeroPrefixIfNeeded(day)}")
    }

private fun appendZeroPrefixIfNeeded(num: Int?) = if (num!! < 10) "0$num" else "$num"

private fun getEventStatsFile(date: String): File? =
    Path(DATABASE_DIRECTORY)
        .toFile()
        .walkTopDown()
        .firstOrNull { it.name.contains(date) }

