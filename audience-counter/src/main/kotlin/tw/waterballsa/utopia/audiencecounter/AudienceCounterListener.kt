package tw.waterballsa.utopia.audiencecounter

import net.dv8tion.jda.api.entities.GuildWelcomeScreen.Channel
import net.dv8tion.jda.api.entities.Webhook.ChannelReference
import net.dv8tion.jda.api.entities.channel.ChannelField
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.requests.restaction.GuildAction.ChannelData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.onEnd
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt
import java.nio.channels.Channels
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds


private const val TIME_LENGTH = "time-length"
private val channelIdToChannelHighestAudience = hashMapOf<String, Int>()
private val channelIdToChannelAverageAudience = hashMapOf<String, Int>()

@Component
class AudienceCounterListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {
    private var timerTask: TimerTask? = null

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash("audience", "audience command")
                .addSubcommands(
                    SubcommandData("counter", "count the recent voice channel audience amount")
                        .addOption(OptionType.INTEGER, TIME_LENGTH, "Time is calculated in minutes.", true)
                )
                .addSubcommands(
                    SubcommandData("counter-stop", "Stop the counter")
                )
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != "audience counter" && fullCommandName != "audience counter-stop") {
                return
            }

            if (!member!!.roles.any { it.id == wsa.wsaAlphaRoleId }) {
                reply("You are not **Alpha**!!!").setEphemeral(true).queue()
                return
            }

            when (subcommandName) {
                "counter" -> {
                    if (timerTask != null) {
                        reply("Counter is already running.").setEphemeral(true).queue()
                        return
                    }

                    val recordPeriodTime = getOptionAsPositiveInt(TIME_LENGTH)?.toLong() ?: return
                    val currentTime = LocalDateTime.now()
                    val startRecordTime = currentTime.truncatedTo(ChronoUnit.MINUTES).toDate()
                    val endRecordTime = currentTime.plusMinutes(recordPeriodTime).toDate()

                    reply("Counter started!").queue {
                        scheduledRecordAudienceAmount(startRecordTime, endRecordTime)
                    }
                }

                "counter-stop" -> {
                    if (timerTask == null) {
                        reply("Counter is not running.").setEphemeral(true).queue()
                        return
                    }

                    stopCounter()
                    reply("Counter stopped!").queue()
                }

                else -> {
                    reply("Unknown command").setEphemeral(true).queue()
                }
            }
        }
    }
}

private fun SlashCommandInteractionEvent.scheduledRecordAudienceAmount(startRecordTime: Date, endRecordTime: Date) =
    timerTask {
        recordAverageAudience()
        recordHighestAudience()
    }

private fun SlashCommandInteractionEvent.recordAverageAudience() =
    timerTask {
        val commandVoiceChannel = channel.asVoiceChannel()
        val audienceAmount = commandVoiceChannel.members.size
        val averageAudience = channelIdToChannelAverageAudience[commandVoiceChannel.id] ?: audienceAmount
        channelIdToChannelAverageAudience[commandVoiceChannel.id] = max(audienceAmount, averageAudience)
    }

private fun SlashCommandInteractionEvent.recordHighestAudience() =
    timerTask {
        val commandVoiceChannel = channel.asVoiceChannel()
        val audienceAmount = commandVoiceChannel.members.size
        val highestAudience = channelIdToChannelHighestAudience[commandVoiceChannel.id] ?: audienceAmount
        channelIdToChannelHighestAudience[commandVoiceChannel.id] = max(audienceAmount, highestAudience)
    }

private fun SlashCommandInteractionEvent.sendResultInVoiceChannel() =
    timerTask {
        val commandVoiceChannel = channel.asVoiceChannel()
        val audienceAmount = commandVoiceChannel.members.size
        val highestAudience = channelIdToChannelHighestAudience[commandVoiceChannel.id] ?: audienceAmount
        commandVoiceChannel.sendMessage("The highest audience amounts: **$highestAudience**").queue {
            channelIdToChannelHighestAudience.remove(commandVoiceChannel.id)
        }
    }

private fun SlashCommandInteractionEvent.stopCounter() {
    // Implement the logic to stop the counter if needed
}
