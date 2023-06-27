package tw.waterballsa.utopia.automaticchannelcommenttracking

import com.fasterxml.jackson.databind.ObjectMapper
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.automaticchannelcommenttracking.repository.JsonRepository
import tw.waterballsa.utopia.automaticchannelcommenttracking.repository.Query
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import java.time.*
import java.time.format.DateTimeFormatter

// 6
// 27 26 25 24 26 22  21
// 45 92 24 68 87 219 198
const val BUFFER_COMMAND_TAG = "buffer"
const val QUERY_COMMAND_NAME = "query"
const val RETRIEVE_COMMAND_NAME = "retrieve"

val logger = KotlinLogging.logger {}

@Component
class AutomaticChannelCommentTrackingListener(
    private val wsa: WsaDiscordProperties,
    private val jda: JDA,
    private val jsonRepository: JsonRepository,
    private val jsonMapper: ObjectMapper
) : UtopiaListener() {

    companion object {
        private val taipeiZoneId: ZoneId = ZoneId.of("Asia/Taipei")

        private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            incrementMessageCount(message)
        }
    }

    private fun incrementMessageCount(message: Message) {
        jsonRepository.incrementCountByQuery(Query(message.toDate(), message.author.id, message.channel.id))
    }

    private fun Message.toDate(): String = timeCreated.atZoneSameInstant(taipeiZoneId).format(dateFormat)

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(BUFFER_COMMAND_TAG, "只有buffer有權限")
                .addSubcommands(
                    SubcommandData(QUERY_COMMAND_NAME, "查詢留言數")
                        .addOption(OptionType.INTEGER, "year", "輸入年分", false)
                        .addOption(OptionType.INTEGER, "mouth", "輸入月分", false)
                        .addOption(OptionType.INTEGER, "day", "輸入日期", false)
                        .addOption(OptionType.CHANNEL, "channel", "輸入頻道", false)
                        .addOption(OptionType.USER, "user", "輸入使用者", false),

                    SubcommandData(RETRIEVE_COMMAND_NAME, "把頻道開啟以來的留言寫入資料庫")
                        .addOption(OptionType.CHANNEL, "channel", "輸入頻道", true)
                )
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            val (tag, command) = splitCommandName(" ")

            if (tag != BUFFER_COMMAND_TAG) {
                return
            }

            if (member.isNotAlphaMember()) {
                reply("你沒有權限").queue()
                return
            }

            when (command) {
                QUERY_COMMAND_NAME -> handleQueryCommand()
                RETRIEVE_COMMAND_NAME -> handleRetrieveCommand()
                else -> return
            }
        }
    }

    private fun SlashCommandInteractionEvent.splitCommandName(delimiters: String): Pair<String, String> {
        val result = fullCommandName.split(" ")
        if (result.size != 2) {
            return Pair("", "")
        }
        return Pair(result[0], result[1])
    }

    private fun Member?.isNotAlphaMember(): Boolean {
        val roles = this?.roles?.mapNotNull { it.id } ?: return false
        return wsa.wsaAlphaRoleId !in roles
    }

    private fun SlashCommandInteractionEvent.handleQueryCommand() {

        val date = getOptionDate()
        val channelId = getOptionChannelId()
        val userId = getOptionUserId()

        val result = jsonRepository.findByQuery(Query(date, userId, channelId))

        reply(jsonMapper.writeValueAsString(result)).setEphemeral(true).queue()
    }

    private fun SlashCommandInteractionEvent.getOptionDate(): String {
        val year = getOption("year")?.asInt ?: return Query.ignore
        val month = getOption("mouth")?.asInt ?: return Query.ignore
        val day = getOption("day")?.asInt ?: return Query.ignore

        return getDate(year, month, day)
    }

    private fun getDate(year: Int, month: Int, day: Int): String {
        try {
            return LocalDate.of(year, month, day)?.format(dateFormat) ?: return Query.ignore
        } catch (e: DateTimeException) {
            return Query.ignore
        }
    }

    private fun SlashCommandInteractionEvent.getOptionChannelId(): String =
        getOption("channel")?.asChannel?.id ?: Query.ignore

    private fun SlashCommandInteractionEvent.getOptionUserId(): String = getOption("user")?.asUser?.id ?: Query.ignore

    private fun SlashCommandInteractionEvent.handleRetrieveCommand() {
        val optionChannel = getOptionTextChannel() ?: run {
            reply("你輸入的不是文字頻道").queue()
            return
        }

        deferReply().setEphemeral(true).queue()

        val messages = optionChannel.getAllMessages().ifEmpty { return }

        jsonRepository.removeByQuery(Query(channelId = optionChannel.id))

        messages.forEach {
            incrementMessageCount(it)
        }

        hook.editOriginal("結束").queue()
    }

    private fun SlashCommandInteractionEvent.getOptionTextChannel(): TextChannel? =
        getOption("channel")?.asChannel?.asTextChannel()

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




