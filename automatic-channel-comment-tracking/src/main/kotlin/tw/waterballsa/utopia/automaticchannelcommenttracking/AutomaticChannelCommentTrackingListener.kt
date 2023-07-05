package tw.waterballsa.utopia.automaticchannelcommenttracking

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.automaticchannelcommenttracking.repository.CommentCountRepository
import tw.waterballsa.utopia.automaticchannelcommenttracking.repository.Query
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import java.time.*
import java.time.format.DateTimeFormatter

val logger = KotlinLogging.logger {}

private const val BUFFER_COMMAND_TAG = "buffer"
private const val QUERY_COMMAND_NAME = "query"
private const val RETRIEVE_COMMAND_NAME = "retrieve"

private const val YEAR = "year"
private const val MONTH = "month"
private const val DAY = "day"

private const val CHANNEL = "channel"
private const val USER = "user"

@Component
class AutomaticChannelCommentTrackingListener(
    private val wsa: WsaDiscordProperties,
    private val jda: JDA
) : UtopiaListener() {


    companion object {
        private val jsonMapper = ObjectMapper()
        private val commentCountRepository = CommentCountRepository(jsonMapper)

        private val TAIPEI_ZONE_ID = ZoneId.of("Asia/Taipei")
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    }

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(BUFFER_COMMAND_TAG, "只有buffer有權限")
            .addSubcommands(
                SubcommandData(QUERY_COMMAND_NAME, "查詢留言數")
                    .addOptions(
                        OptionData(OptionType.INTEGER, YEAR, "輸入年分", false),
                        OptionData(OptionType.INTEGER, MONTH, "輸入月分", false),
                        OptionData(OptionType.INTEGER, DAY, "輸入日期", false),
                        OptionData(OptionType.CHANNEL, CHANNEL, "輸入頻道", false),
                        OptionData(OptionType.USER, USER, "輸入使用者", false)
                    ),
                SubcommandData(RETRIEVE_COMMAND_NAME, "把頻道開啟以來的留言寫入資料庫")
                    .addOption(OptionType.CHANNEL, CHANNEL, "輸入頻道", true)
            )
    )

    override fun onMessageReceived(event: MessageReceivedEvent) = incrementMessageCount(event.message)

    private fun incrementMessageCount(message: Message) = with(message) {
        commentCountRepository.incrementCountByQuery(Query(toDate(), author.id, channel.id))
    }

    private fun Message.toDate(): String = timeCreated.atZoneSameInstant(TAIPEI_ZONE_ID).format(DATE_FORMAT)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            val (tag, command) = splitCommandName(" ")

            when {
                tag != BUFFER_COMMAND_TAG -> {
                    return
                }

                member.isAlphaMember().not() -> {
                    reply("你沒有權限").queue()
                    return
                }
            }

            if (member.isAlphaMember()) {

            }

            when (command) {
                QUERY_COMMAND_NAME -> handleQueryCommand()
                RETRIEVE_COMMAND_NAME -> handleRetrieveCommand()
                else -> return
            }
        }
    }

    private fun SlashCommandInteractionEvent.splitCommandName(delimiters: String): Pair<String, String> =
        fullCommandName.split(delimiters).run { if (size == 2) first() to last() else "" to "" }

    private fun Member?.isAlphaMember(): Boolean = this?.roles?.any { it.id == wsa.wsaAlphaRoleId } ?: false

    private fun SlashCommandInteractionEvent.handleQueryCommand() {

        val date = getOptionDate()
        val channelId = getOptionChannelId()
        val userId = getOptionUserId()

        val result = commentCountRepository.findByQuery(Query(date, userId, channelId))

        reply(jsonMapper.writeValueAsString(result)).setEphemeral(true).queue()
    }

    private fun SlashCommandInteractionEvent.getOptionDate(): String {
        val year = getOption(YEAR)?.asInt ?: return Query.IGNORE
        val month = getOption(MONTH)?.asInt ?: return Query.IGNORE
        val day = getOption(DAY)?.asInt ?: return Query.IGNORE

        return getDate(year, month, day)
    }

    private fun getDate(year: Int, month: Int, day: Int): String {
        return try {
            LocalDate.of(year, month, day)?.format(DATE_FORMAT) ?: return Query.IGNORE
        } catch (e: DateTimeException) {
            Query.IGNORE
        }
    }

    private fun SlashCommandInteractionEvent.getOptionChannelId(): String =
        getOption(CHANNEL)?.asChannel?.id ?: Query.IGNORE

    private fun SlashCommandInteractionEvent.getOptionUserId(): String = getOption(USER)?.asUser?.id ?: Query.IGNORE

    private fun SlashCommandInteractionEvent.handleRetrieveCommand() {
        val optionChannel = getOptionTextChannel() ?: run {
            reply("你輸入的不是文字頻道").queue()
            return
        }

        deferReply().setEphemeral(true).queue()

        val messages = optionChannel.getAllMessages().ifEmpty { return }

        commentCountRepository.removeByQuery(Query(channelId = optionChannel.id))

        messages.forEach { message -> incrementMessageCount(message) }

        hook.editOriginal("結束").queue()
    }

    private fun SlashCommandInteractionEvent.getOptionTextChannel(): TextChannel? =
        getOption(CHANNEL)?.asChannel?.asTextChannel()

    private fun TextChannel.getAllMessages(): List<Message> {
        val messages = mutableListOf<Message>(retrieveMessageById(latestMessageId).complete())

        var messageIterator = latestMessageId

        while (true) {
            val history = getHistoryBefore(messageIterator, 100).complete().retrievedHistory.filterNotNull()
            messages.addAll(history)
            history.ifEmpty { return messages }
            messageIterator = history.last().id
        }
    }
}
