package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithValidation
import tw.waterballsa.utopia.jda.listener
import java.io.File
import kotlin.io.path.Path

private const val OPTION_EVENT_DATE = "event-date"
private const val DATABASE_DIRECTORY = "data/gaas/participation-stats"

fun getMaxAndAvgParticipantsAtSpecificDate(wsaDiscordProperties: WsaDiscordProperties) = listener {
    command {
        Commands.slash("gaas", "Query GaaS Event Stats")
            .addSubcommands(
                SubcommandData("stats-avg-and-max", "Get Avg And Max Participants Number at Specific Date")
                    .addOption(OptionType.STRING, OPTION_EVENT_DATE, "The event date. Format: yyyy-MM-dd", true)
            )
    }

    on<SlashCommandInteractionEvent> {
        val alphaRoleId = wsaDiscordProperties.wsaAlphaRoleId
        val commandUser = member!!

        takeUnless { commandUser.isAlphaMember(alphaRoleId) }
            ?.run {
                replayEphemerally("權限不足")
                return@on
            }

        val date =
            getOptionAsStringWithValidation(OPTION_EVENT_DATE, "Invalid Date Format", validateDateFormat)
                ?: run {
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
                .joinToString("\n")

            replayEphemerally(avgAndMax)
        }
    }
}

private fun getEventStatsFile(date: String): File? =
    Path(DATABASE_DIRECTORY)
        .toFile()
        .walkTopDown()
        .firstOrNull { it.name.contains(date) }

private val validateDateFormat: (String) -> Boolean =
    // RegEx validate format: yyyy-MM-dd
    { Regex("""^\d{4}-\d{2}-\d{2}$""").matches(it) }

private fun Member.isAlphaMember(alphaRoleId: String): Boolean =
    alphaRoleId in roles.mapNotNull { it.id }

private fun SlashCommandInteractionEvent.replayEphemerally(message: String) =
    reply(message).setEphemeral(true).queue()
