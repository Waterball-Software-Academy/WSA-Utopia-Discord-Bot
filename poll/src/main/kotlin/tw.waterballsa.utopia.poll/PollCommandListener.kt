package tw.waterballsa.utopia.poll

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.entities.emoji.EmojiUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntInRange
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithValidation
import java.awt.Color
import java.util.concurrent.TimeUnit

private const val OPTION_TIME = "time"

private const val OPTION_TIMEUNIT = "time-unit"

private const val OPTION_QUESTION = "question"

private const val OPTION_OPTIONS = "options"

private val EMOJI_UNICODES: Array<String> = arrayOf("0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣")

/**
 * /poll time=1 timeUnit=minutes question="Which operating system do you prefer?" options="Windows,MacOS,Linux,Other"
 * @author johnny@waterballsa.tw
 */
@Component
class PollCommandListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {
    // embedded message's id to polling session
    private val messageIdToSession: MutableMap<String, PollingSession> = mutableMapOf()

    override fun commands(): List<CommandData> {
        return listOf(
                Commands.slash("poll", "<TODO>")
                        .addRequiredOption(OptionType.INTEGER, OPTION_TIME, "The duration of the poll session")
                        .addRequiredOption(OptionType.STRING, OPTION_TIMEUNIT, "(Day | Minute | Second)")
                        .addRequiredOption(OptionType.STRING, OPTION_QUESTION, "Question")
                        .addRequiredOption(OptionType.STRING, OPTION_OPTIONS, "Options (split by comma)")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (!fullCommandName.startsWith("poll")) {
                return
            }
            val setting = getPollingSetting()

            val message = channel.sendMessageEmbeds(setting.toMessageEmbeds()).complete()
            val session = PollingSession(id = message.id, setting = setting)
            messageIdToSession[message.id] = session
            options.forEachIndexed { i, _ ->
                message.addReaction(Emoji.fromUnicode(EMOJI_UNICODES[i])).complete()
            }
        }
    }


    private fun SlashCommandInteractionEvent.getPollingSetting(): PollingSetting {
        val time = getOptionAsIntInRange(OPTION_TIME, 0..500)
        val timeUnit = getOptionAsStringWithValidation(OPTION_TIMEUNIT, "should be one of (Day | Minute | Second)") {
            TimeUnit.values().any { unit -> unit.name == it.uppercase() }
        }.let { TimeUnit.valueOf(it!!.uppercase()) }

        val question = getOption(OPTION_QUESTION)?.asString
        val options = getOption(OPTION_OPTIONS)?.asString?.split(Regex("\\s*,\\s*"))

        return PollingSetting(time!!, timeUnit, question!!, options!!)
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            val session = messageIdToSession[messageId] ?: return
            if (userId == jda.selfUser.id) {
                return
            }
            session.vote(Vote(userId, emoji))
        }
    }
}

data class PollingSetting(val time: Int, val timeUnit: TimeUnit, val question: String, val options: List<String>) {
    private val optionsMessageBody = options.mapIndexed { i, option ->
        "${EMOJI_UNICODES[i]} $option"
    }.joinToString("\n")


    fun toMessageEmbeds(): Collection<MessageEmbed> =
            listOf(
                    EmbedBuilder()
                            .setTitle("Polling")
                            .setDescription(question)
                            .setColor(Color.GREEN)
                            .build(),
                    EmbedBuilder()
                            .setTitle("Options")
                            .setDescription(optionsMessageBody)
                            .setColor(Color.RED)
                            .build(),
                    EmbedBuilder()
                            .setTitle("Settings")
                            .setDescription("Session ends in $time $timeUnit.")
                            .setColor(Color.BLUE)
                            .build(),
            )
}

data class Vote(val userId: String, val emoji: EmojiUnion)

class PollingSession(
        val id: String, private val setting: PollingSetting) {
    private val voterIdToVotedOptionIndices = hashMapOf<String, MutableList<Int>>()
    fun vote(vote: Vote) {
        val emojiIndex = EMOJI_UNICODES.indexOf(vote.emoji.name)
        if (emojiIndex < 0) {
            return
        }
        voterIdToVotedOptionIndices.computeIfAbsent(vote.userId) { mutableListOf() }
                .add(emojiIndex)
    }
}

private fun SlashCommandData.addRequiredOption(type: OptionType, name: String, description: String) =
        addOption(type, name, description, true)
