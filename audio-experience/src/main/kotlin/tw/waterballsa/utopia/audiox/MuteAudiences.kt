package tw.waterballsa.utopia.audiox

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Widget
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener

private const val OPTION_AUDIENCE_NAME = "audience"
private const val OPTION_ROLE_NAME = "role"
private const val MUTE_SLASH = "mute"
private const val AUDIENCES_SUBCOMMAND = "audiences"
private const val MUTE_AUDIENCES_COMMAND = "$MUTE_SLASH $AUDIENCES_SUBCOMMAND"
private const val REVOKED_SUB_COMMAND = "revoked"
private const val MUTE_REVOKED_COMMAND = "$MUTE_SLASH $REVOKED_SUB_COMMAND"

private val log = KotlinLogging.logger {}
private var currentCommandName = ""

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
        log.info { "[Mute Command]: Command in $channel" }
        log.info { "[Mute Command]: User in $memberChannel" }

        if (isNotMuteCommand() && isNotVoiceChannel()) {
            return@on
        }

        val voiceChannel = channel.asVoiceChannel();

        when (fullCommandName) {
            MUTE_AUDIENCES_COMMAND -> {
                currentCommandName = MUTE_AUDIENCES_COMMAND
                val role = getOption(OPTION_ROLE_NAME);
                val audience = getOption(OPTION_AUDIENCE_NAME);

                voiceChannel.members.forEach {
                    when {
                        isUnmuteAudiences(it, audience) -> muteMember(it, false)
                        isUnmuteRole(it, role) -> muteMember(it, false)
                        else -> muteMember(it, true)
                    }
                }
                this.reply("Mute audience voice !!").queue()
            }

            MUTE_REVOKED_COMMAND -> {
                currentCommandName = MUTE_REVOKED_COMMAND
                voiceChannel.members.forEach { muteMember(it, false) }
                this.reply("Unmute voice !!").queue()
            }
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

private fun isUnmuteAudiences(member: Member, audience: OptionMapping?): Boolean {
    return member.id == audience?.asUser?.id
}

private fun isUnmuteRole(member: Member, role: OptionMapping?): Boolean {
    return member.roles.contains(role?.asRole);
}

private fun muteMember(member: Member, isMute: Boolean) {
    val memberName = member.nickname ?: member.effectiveName
    val memberId = member.id
    val muteLabel = if (isMute) "Mute" else "Unmute"

    member.mute(isMute)
        .queue { log.info { "[$currentCommandName]: $muteLabel {\"memberName\":\"${memberName}\", \"memberId\":\"${memberId}\"} voice !!" } }
}

