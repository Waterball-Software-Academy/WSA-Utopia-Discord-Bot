package tw.waterballsa.utopia.audiobreakout

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntInRange
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntWithValidation
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithLimitedLength
import tw.waterballsa.utopia.jda.listener
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


private const val OPTION_ROOM_SIZE = "room-size"

private const val OPTION_COUNTDOWN = "countdown"

private const val OPTION_ROOM_NAME = "room-name"

private val roomIdToMemberIds = mutableMapOf<String, List<String>>()

private val timer = Timer()

private val log = KotlinLogging.logger {}

fun audioBreakoutRoom() = listener {
    command {
        Commands.slash("audio", "Audio Breakout Rooms")
                .addSubcommands(
                        SubcommandData("breakout", "Create breakout rooms.")
                                .addOption(OptionType.INTEGER, OPTION_ROOM_SIZE, "Number of members per room.", true)
                                .addOption(OptionType.INTEGER, OPTION_COUNTDOWN, "Countdown time in seconds.", true)
                                .addOption(OptionType.STRING, OPTION_ROOM_NAME, "The name prefix of per room.", false))
    }

    on<SlashCommandInteractionEvent> {
        // if the member is currently in a temporary room, he can't use this command
        if (validateMemberShouldNotAlreadyBeInRoom()) {
            return@on
        }

        val mainChannel = member?.voiceState?.channel ?: return@on
        val mainCategory = mainChannel.parentCategory
        val guild = mainChannel.guild
        val roomChannels = mutableListOf<VoiceChannel>()
        val members = mainChannel.members.filterNot { it.user.isBot }
        val roomSize = getOptionAsIntWithValidation(OPTION_ROOM_SIZE, "less than a half of the member count in the host channel.") {
            it <= members.size / 2
        } ?: return@on
        val countdownTimeInSeconds = getOptionAsIntInRange(OPTION_COUNTDOWN, 20..3600) ?: return@on
        val roomNamePrefix = getOptionAsStringWithLimitedLength(OPTION_ROOM_NAME, 1..10) ?: "Room"
        val memberCount = members.size
        val groupCount = memberCount / roomSize
        val roomId = UUID.randomUUID().toString()
        roomIdToMemberIds[roomId] = members.map { it.id }

        log.info { "[breaking rooms] {\"roomId\"=\"$roomId\", \"$OPTION_ROOM_SIZE\":$roomSize, $OPTION_ROOM_NAME\":\"$roomNamePrefix\", \"$OPTION_COUNTDOWN\":$countdownTimeInSeconds}" }

        // break out rooms
        for (i in 0 until groupCount) {
            val roomNumber = i + 1
            val startIndex = i * roomSize
            val endIndex = min((i + 1) * roomSize, memberCount)
            val groupMembers = members.subList(startIndex, endIndex)
            val groupChannelName = "┗ $roomNumber • \uD83D\uDC65 $roomNamePrefix"

            val groupChannel = if (mainCategory == null) guild.createVoiceChannel(groupChannelName).complete()
            else mainCategory.createVoiceChannel(groupChannelName).complete()

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

        reply("Rooms break-out successfully!").queue()

        timer.schedule(timerTask {
            for (groupChannel in roomChannels) {
                groupChannel.members.forEach { guild.moveVoiceMember(it, mainChannel).complete() }
                groupChannel.delete().queue()
            }
            roomIdToMemberIds.remove(roomId)
            log.info { "[assembled] {\"roomId\"=\"$roomId\"}" }
        }, countdownTimeInSeconds.seconds.inWholeMilliseconds)

    }
}

private fun SlashCommandInteractionEvent.validateMemberShouldNotAlreadyBeInRoom(): Boolean {
    val memberFound = roomIdToMemberIds.values
            .flatten().find { it == member?.id }
    if (memberFound != null) {
        reply("You cannot use the breakout function when you are in a break-out room.").complete()
        log.info { "[member should not be in a room] {\"memberId\"=\"${member?.id}\"}" }
    }
    return memberFound != null
}

private fun moveVoiceMember(guild: Guild, member: Member, voiceChannel: VoiceChannel) {
    try {
        guild.moveVoiceMember(member, voiceChannel).complete()
    } catch (ignored: ErrorResponseException) {
    }
}
