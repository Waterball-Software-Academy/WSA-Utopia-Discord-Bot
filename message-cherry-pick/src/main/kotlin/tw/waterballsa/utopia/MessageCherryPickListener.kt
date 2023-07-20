package tw.waterballsa.utopia

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType.STRING
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.MessageCherryPickListener.Companion.CHANNEL_NAME
import tw.waterballsa.utopia.MessageCherryPickListener.Companion.END_TIME
import tw.waterballsa.utopia.MessageCherryPickListener.Companion.MARK_EMOJI
import tw.waterballsa.utopia.MessageCherryPickListener.Companion.START_TIME
import tw.waterballsa.utopia.domain.CherryPick
import tw.waterballsa.utopia.domain.DateTimeRange
import tw.waterballsa.utopia.domain.rangeTo
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.addRequiredOption
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithValidation
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

private const val HYPHEN_DATE_PATTERN = "yyyy-MM-dd"

@Component
class MessageCherryPickListener : UtopiaListener() {

    private val cherryPickRepository = CherryPickRepository()

    companion object {
        private const val MESSAGE = "message"
        private const val CHERRY_PICK = "cherry-pick"
        const val START_TIME = "start-time"
        const val END_TIME = "end-time"
        private const val CHERRY_PICK_TO = "cherry-pick-to"
        const val CHANNEL_NAME = "channel-name"
        const val MARK_EMOJI = "🍒"
    }

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(MESSAGE, "message command, Warning: if the bot shutdown, Your cherry-pick won't save your change.")
                .addSubcommands(
                    SubcommandData(CHERRY_PICK, "to cherry pick message")
                        .addRequiredOption(
                            STRING,
                            START_TIME,
                            "the start time of the cherry-pick range."
                        )
                        .addOption(STRING, END_TIME, "the end time of the cherry-pick range"),
                    SubcommandData(CHERRY_PICK_TO, "to cherry pick message to another channel")
                        .addRequiredOption(STRING, CHANNEL_NAME, "destination of channel name")
                )
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != "$MESSAGE $CHERRY_PICK" &&
                fullCommandName != "$MESSAGE $CHERRY_PICK_TO"
            ) {
                return
            }

            when (subcommandName) {
                CHERRY_PICK -> cherryPick()
                CHERRY_PICK_TO -> cherryPickTo()
            }
        }
    }

    private fun SlashCommandInteractionEvent.cherryPick() {
        val userId = user.id
        val dateTimeRange = dateTimeRange ?: return

        if (dateTimeRange.isStartTimeAfterEndTime()) {
            replyEphemerally("start time must be before end time.")
            return
        }

        if (cherryPickRepository.exists(userId)) {
            replyEphemerally("you are using cherry-pick.")
            return
        }

        cherryPickRepository.save(CherryPick(userId, dateTimeRange))
        replyEphemerally("starting cherry-pick, and ${dateTimeRange}, using $MARK_EMOJI to mark message which you want to cherry pick.")
    }

    private fun SlashCommandInteractionEvent.cherryPickTo() {
        val userId = user.id
        val user = cherryPickRepository.findById(userId) ?: run {
            replyEphemerally("please use /$MESSAGE $CHERRY_PICK first.")
            return
        }

        val channelName = getChannelName()
        val cherryPickChannel = createThreadChannel(channelName) ?: return

        replyEphemerally("start cherry-pick to $channelName")

        cherryPickMessages(user.dateTimeRange, cherryPickChannel)

        cherryPickRepository.delete(userId)
    }
}

private val SlashCommandInteractionEvent.dateTimeRange: DateTimeRange?
    get() = run {
        val startDate = getValidateDateString(START_TIME) ?: return null
        val endDate = getValidateDateString(END_TIME)
        val startDateTime = startDate.toTaiwanTime()
        val endDateTime = endDate?.toTaiwanTime() ?: startDateTime.plusDays(1)
        return startDateTime.rangeTo(endDateTime)
    }

private fun GenericCommandInteractionEvent.getValidateDateString(optionName: String): String? =
    getOptionAsStringWithValidation(optionName, "Date format is $HYPHEN_DATE_PATTERN") { it.validateDate() }

private fun String.validateDate(): Boolean =
    this matches Regex("""^\d{4}-(0[1-9]|1[0-2])-([0-2]\d|3[01])""")

private fun String.toTaiwanTime(): OffsetDateTime =
    LocalDate.parse(this, ISO_LOCAL_DATE)
        .atStartOfDay()
        .atZone(ZoneId.of("Asia/Taipei"))
        .toOffsetDateTime()

private fun SlashCommandInteractionEvent.getChannelName() =
    getOptionAsStringWithValidation(
        CHANNEL_NAME,
        "thread name is invalid."
    ) { it.isNotBlank() }!!

private fun SlashCommandInteractionEvent.createThreadChannel(channelName: String) =
    guild!!.getTextChannelById(channel.id)
        ?.createThreadChannel(channelName)
        ?.complete()

private fun SlashCommandInteractionEvent.cherryPickMessages(
    dateTimeRange: DateTimeRange,
    cherryPickChannel: ThreadChannel
) {
    channel.iterableHistory
        .filter { it.isUserSelected(dateTimeRange) }
        .sortedBy { it.timeCreated }
        .map { it.createEmbedMessage() }
        .forEach { cherryPickChannel.sendMessageEmbeds(it).queue() }
}

private fun Message.isUserSelected(dateTimeRange: DateTimeRange) =
    dateTimeRange.contains(timeCreated) && hasCherryPickReaction() && contentDisplay.isNotBlank()

private fun Message.hasCherryPickReaction(): Boolean =
    reactions.any { it.emoji.asReactionCode == MARK_EMOJI }

private fun Message.createEmbedMessage(): MessageEmbed =
    EmbedBuilder().apply {
        setTimestamp(timeCreated)
        setUrl(jumpUrl)
        setAuthor(
            author.effectiveName,
            author.effectiveAvatarUrl,
            author.avatarUrl
        )
        setDescription(contentDisplay)
    }.build()
