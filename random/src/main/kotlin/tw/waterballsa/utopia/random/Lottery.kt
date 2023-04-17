package tw.waterballsa.utopia.random

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsIntInRange
import tw.waterballsa.utopia.jda.listener

private const val OPTION_NUMBER_NAME = "number"
private const val OPTION_ROLE_NAME = "role"

private val log = KotlinLogging.logger {}

fun lottery(wsa: WsaDiscordProperties) = listener {
    command {
        Commands.slash("random", "Random")
            .addSubcommands(
                SubcommandData("lottery", "Lottery")
                    .addOption(OptionType.INTEGER, "number", "Number of choose members per room.", true)
                    .addOption(OptionType.ROLE, "role", "Only select specific role in this round", false)
            )
    }

    on<SlashCommandInteractionEvent> {
        // Command
        if (fullCommandName != "random lottery") {
            return@on
        }

        log.info { "[$fullCommandName]: ${member?.user} use command in $channel" }

        val number = getOptionAsIntInRange(OPTION_NUMBER_NAME, 1..100) ?: return@on
        val role = getOption(OPTION_ROLE_NAME)?.asRole

        // Check channel's instance
        val mainChannel = when (channel) {
            is TextChannel -> channel.asTextChannel()
            is VoiceChannel -> channel.asVoiceChannel()
            else -> {
                reply("${channel.name} 不支援指令 /$fullCommandName").queue()
                return@on
            }
        }

        // Filter qualified members according to the 'role' parameter
        val members = mainChannel.members.filter { isQualifiedMember(it, role) }
        if (members.isEmpty()) {
            reply("人數不足").queue()
            return@on
        }

        val selectedMembers = getRandomMember(members, number)

        reply(
            selectedMembers.joinToString(
                separator = " ",
                prefix = "恭喜 ",
                postfix = " 被選中囉！！",
                transform = { it.asMention })
        ).queue()
    }
}

private fun <T> getRandomMember(list: Collection<T>, x: Int): List<T> = list.shuffled().take(x)

private fun isQualifiedMember(member: Member, role: Role?): Boolean =
    (role == null || member.roles.contains(role)) && !member.user.isBot
