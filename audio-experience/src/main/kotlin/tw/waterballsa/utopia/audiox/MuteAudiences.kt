package tw.waterballsa.utopia.audiox

import net.dv8tion.jda.api.entities.Widget.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener

private const val OPTION_AUDIENCE_NAME = "audience"
private const val OPTION_ROLE_NAME = "role"

fun muteAudiences(wsa: WsaDiscordProperties) = listener {
    // hello world <arg1>
    // <feature-module's name> <feature's name> <arg1> <arg2> ....

    // mute audiences
    // allow alpha members
    // allow speakers
    command {
        Commands.slash("mute", "Mute")
            .addSubcommands(
                SubcommandData("audiences", "Mute Audiences")
                    .addOption(OptionType.USER, OPTION_AUDIENCE_NAME, "Allow who to voice")
                    .addOption(OptionType.ROLE, OPTION_ROLE_NAME, "Allow role to voice"),
                SubcommandData("revoked", "UnMute")
            )
    }

    on<SlashCommandInteractionEvent>{
        // 監聽事件
        // reactive programming

        // sync: block to complete()
        // async: execute to queue()
        if ((fullCommandName != "mute audiences" || fullCommandName != "mute revoked" ) && channel is VoiceChannel){
            return@on
        }

        val voiceChannel = channel.asVoiceChannel();

        if (fullCommandName == "mute audiences"){
            val role = getOption(OPTION_ROLE_NAME);
            val audience = getOption(OPTION_AUDIENCE_NAME);

            voiceChannel.members.forEach{
                if (it.id == audience?.asUser?.id){
                    it.mute(false).queue()
                }
                else if (it.roles.contains(role?.asRole)) {
                    it.mute(false).queue()
                }
                else
                    it.mute(true).queue()
            }
            this.reply("Mute audience voice !!").queue()
        }
        else if (fullCommandName == "mute revoked"){
            voiceChannel.members.forEach{ it.mute(false).queue() }
            this.reply("UnMute voice !!").queue()
        }
    }
}


