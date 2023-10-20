package tw.waterballsa.utopia.audiencecounter

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.onEnd
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import net.dv8tion.jda.api.entities.Member
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds



private const val TIME_LENGTH = "time-length"
private val channelIdToChannelHighestAudience = hashMapOf<String, Int>()

@Component
class AudienceCounterListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash("audience", "audience command")
                .addSubcommands(
                    SubcommandData("counter", "count the resent voice channel audience amount")
                        .addOption(OptionType.INTEGER, TIME_LENGTH, "Time is calculated in minutes.", true)
                )
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != "audience counter") {
                return
            }

            if (member.isAlpha()) {
                val recordPeriodTime =
                    getOptionAsPositiveInt(TIME_LENGTH)!!.toLong()
                val currentTime = now()
                val startRecordTime = currentTime.truncatedTo(ChronoUnit.MINUTES).toDate()
                val endRecordTime = currentTime.plusMinutes(recordPeriodTime).toDate()

                reply("counter start!!!").queue {
                    scheduledRecordHighestAudience(startRecordTime, endRecordTime)
                }
            } else {
                replyEphemerally("You should be the Alpha!")
            }
        }
    }
    private fun Member?.isAlpha(): Boolean = this?.roles?.any { it.id == wsa.wsaAlphaRoleId } ?: false
}

private fun SlashCommandInteractionEvent.scheduledRecordHighestAudience(startRecordTime: Date, endRecordTime: Date) {
    Timer().run {
        onStart(recordHighestAudience(), startRecordTime, 5.seconds.inWholeMilliseconds)
        onEnd(sendResultInVoiceChannel(), endRecordTime)
    }
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
