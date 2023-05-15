package tw.waterballsa.utopia.audiobreakout

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntInRange
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntWithValidation
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithLimitedLength
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


private const val OPTION_ROOM_SIZE = "room-size"

private const val OPTION_COUNTDOWN = "countdown"

private const val OPTION_ROOM_NAME = "room-name"

private val timer = Timer()

@Component
class AudioBreakoutListener : UtopiaListener() {
    private val log = KotlinLogging.logger {}
    private val roomIdToMemberIds = mutableMapOf<String, List<String>>()

    override fun commands(): List<CommandData> {
        return listOf(
                Commands.slash("audio", "Audio Breakout Rooms")
                        .addSubcommands(
                                SubcommandData("breakout", "Create breakout rooms.")
                                        .addOption(OptionType.INTEGER, OPTION_ROOM_SIZE, "Number of members per room.", true)
                                        .addOption(OptionType.INTEGER, OPTION_COUNTDOWN, "Countdown time in seconds.", true)
                                        .addOption(OptionType.STRING, OPTION_ROOM_NAME, "The name prefix of per room.", false))
        )
    }

    override fun onSlashCommandInteraction(e: SlashCommandInteractionEvent) {
        with(e) {
            // if the member is currently in a temporary room, he can't use this command
            if (validateMemberShouldNotAlreadyBeInBreakoutRoom()) {
                return
            }

            val mainChannel = member?.voiceState?.channel ?: return
            val mainCategory = mainChannel.parentCategory
            val guild = mainChannel.guild
            val roomChannels = mutableListOf<VoiceChannel>()
            val members = mainChannel.members.filterNot { it.user.isBot }
            val roomSize = getOptionAsIntWithValidation(OPTION_ROOM_SIZE, "less than a half of the member count in the host channel.") {
                it <= members.size / 2
            } ?: return
            val countdownTimeInSeconds = getOptionAsIntInRange(OPTION_COUNTDOWN, 20..3600) ?: return
            val roomNamePrefix = getOptionAsStringWithLimitedLength(OPTION_ROOM_NAME, 1..10) ?: "Room"
            val memberCount = members.size
            val groupCount = memberCount / roomSize
            val roomId = UUID.randomUUID().toString()
            roomIdToMemberIds[roomId] = members.map { it.id }

            deferReply().queue()
            log.info { "[breaking rooms] {\"roomId\"=\"$roomId\", \"$OPTION_ROOM_SIZE\":$roomSize, $OPTION_ROOM_NAME\":\"$roomNamePrefix\", \"$OPTION_COUNTDOWN\":$countdownTimeInSeconds}" }

            // break out rooms
            for (i in 0 until groupCount) {
                val roomNumber = i + 1
                val startIndex = i * roomSize
                val endIndex = min((i + 1) * roomSize, memberCount)
                val groupMembers = members.subList(startIndex, endIndex)
                val groupChannelName = "┗ $roomNumber • \uD83D\uDC65 $roomNamePrefix"

                val groupChannel = mainCategory?.createVoiceChannel(groupChannelName)?.complete()
                        ?: guild.createVoiceChannel(groupChannelName).complete()

                groupMembers.forEach { guild.moveVoiceMember(it, groupChannel).complete() }
                roomChannels.add(groupChannel)
            }

            // move each of remaining member to every room evenly
            for (i in (memberCount - memberCount % roomSize) until memberCount) {
                val roomIndex = if (groupCount == 0) 0 else i % groupCount
                moveVoiceMember(guild, members[i], roomChannels[roomIndex])
            }

            for (groupChannel in roomChannels) {
                val endTimeInSeconds = (currentTimeMillis() + countdownTimeInSeconds).milliseconds.inWholeSeconds
                groupChannel.sendMessage("倒數 $countdownTimeInSeconds 秒鐘，<t:$endTimeInSeconds:t> 集合！").queue()
            }

            hook.editOriginal("Executing Rooms break-out successfully!").queue()

            timer.schedule(timerTask {
                roomChannels.forEach {
                    it.members.forEach { guild.moveVoiceMember(it, mainChannel).complete() }
                    it.delete().queue()
                }
                roomIdToMemberIds.remove(roomId)
                log.info { "[assembled] {\"roomId\"=\"$roomId\"}" }
            }, countdownTimeInSeconds.seconds.inWholeMilliseconds)

        }
    }

    private fun SlashCommandInteractionEvent.validateMemberShouldNotAlreadyBeInBreakoutRoom(): Boolean {
        val memberFound = roomIdToMemberIds.values
                .flatten().find { it == member?.id }
        memberFound
                ?.let {
                    reply("You cannot use the breakout function when you are in a break-out room.").complete()
                    log.info { "[member should not be in a room] {\"memberId\"=\"${member?.id}\"}" }
                }
        return memberFound != null
    }

    private fun moveVoiceMember(guild: Guild, member: Member, voiceChannel: VoiceChannel) {
        try {
            guild.moveVoiceMember(member, voiceChannel).complete()
        } catch (e: ErrorResponseException) {
            log.warn { "[Error during moveVoiceMember] {\"message\":\"${e.message}\"" }
        }
    }
}

