package tw.waterballsa.utopia.audiox

import mu.KotlinLogging
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.jda.listener

private const val OPTION_AUDIENCE_NAME = "audience"
private const val OPTION_ROLE_NAME = "role"
private const val MUTE_SLASH = "mute"
private const val AUDIENCES_SUBCOMMAND = "audiences"
private const val MUTE_AUDIENCES_COMMAND = "$MUTE_SLASH $AUDIENCES_SUBCOMMAND"
private const val REVOKED_SUB_COMMAND = "revoked"
private const val MUTE_REVOKED_COMMAND = "$MUTE_SLASH $REVOKED_SUB_COMMAND"

private val log = KotlinLogging.logger {}

fun muteAudiences() = listener {
    /*
    mute audiences: The command allows the user to mute audiences
        Optional -> audience: Allow the audience to unmute
        Optional -> role: Allow all members of a role to unmute
    mute revoked: The command allows the user to revoke mute
    */
    command {
        Commands.slash(MUTE_SLASH, "Mute")
            .addSubcommands(
                SubcommandData(AUDIENCES_SUBCOMMAND, "Mute Audiences")
                    .addOption(OptionType.USER, OPTION_AUDIENCE_NAME, "Allow who to voice", false)
                    .addOption(OptionType.ROLE, OPTION_ROLE_NAME, "Allow role to voice", false),
                SubcommandData(REVOKED_SUB_COMMAND, "Unmute")
            )
    }

    on<SlashCommandInteractionEvent> {
        val memberChannel = member?.voiceState?.channel

        log.info { "[Mute Command]: {\"commandInChannel\":\"$channel\", \"userInChannel\":\"$memberChannel\"}" }
        if (isNotMuteCommand() && isNotVoiceChannel()) {
            return@on
        }

        when (fullCommandName) {
            MUTE_AUDIENCES_COMMAND -> {
                val muteMemberAction: () -> Unit = if (isUnmute()) ::unMuteMember else ::muteMember
                executeMuteCommand(muteMemberAction, "Mute audience voice !!")
            }

            MUTE_REVOKED_COMMAND -> executeMuteCommand({ unMuteMember() }, "Unmute voice !!")
        }
    }
}


private fun SlashCommandInteractionEvent.isNotMuteCommand(): Boolean {
    return fullCommandName != MUTE_AUDIENCES_COMMAND || fullCommandName != MUTE_REVOKED_COMMAND
}

private fun SlashCommandInteractionEvent.isNotVoiceChannel(): Boolean {
    val memberChannelId = member?.voiceState?.channel?.id

    val isVoiceChannel = (memberChannelId != null) && (channel.id == memberChannelId)
    if (!isVoiceChannel) {
        this.reply("This is not a voice channel.").queue()
    }
    return !isVoiceChannel
}

private fun SlashCommandInteractionEvent.isUnmute(): Boolean {
    val audience = getOption(OPTION_AUDIENCE_NAME)
    val role = getOption(OPTION_ROLE_NAME)

    val isUnmuteAudiences = member?.id == audience?.asUser?.id
    val isUnmuteRole = member?.roles?.contains(role?.asRole) == true
    return isUnmuteAudiences || isUnmuteRole
}

private fun SlashCommandInteractionEvent.muteMember() {
    val memberName = member?.nickname ?: member?.effectiveName
    val memberId = member?.id

    member?.mute(true)
        ?.queue { log.info { "[$fullCommandName]: {\"muteLabel\":\"Mute\", \"memberName\":\"${memberName}\", \"memberId\":\"${memberId}\"}" } }
}

private fun SlashCommandInteractionEvent.executeMuteCommand(muteMemberAction: () -> Unit, replyMessage: String) {
    muteMemberAction.invoke()
    this.reply(replyMessage).queue()
}

private fun SlashCommandInteractionEvent.unMuteMember() {
    val memberName = member?.nickname ?: member?.effectiveName
    val memberId = member?.id

    member?.mute(false)
        ?.queue { log.info { "[$fullCommandName]: {\"muteLabel\":\"Unmute\", \"memberName\":\"${memberName}\", \"memberId\":\"${memberId}\"}" } }
}

