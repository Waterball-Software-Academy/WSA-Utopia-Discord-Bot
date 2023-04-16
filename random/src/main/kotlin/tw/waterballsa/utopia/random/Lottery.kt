package tw.waterballsa.utopia.random

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithLimitedLength
import tw.waterballsa.utopia.jda.listener

private const val OPTION_NAME = "number"

fun lottery(wsa: WsaDiscordProperties) = listener {
    // channelId

    // [ /command ]
    // [ /<feature-module's name> <feature's name> <param1> <param2> <param3> ... ]
    // [ /random lottery number ]
    command {
        Commands.slash("random", "Random")
            .addSubcommands(
                SubcommandData("lottery", "Lottery")
                    .addOption(OptionType.INTEGER, "number", "Number of choose members per room.", true)
            )
    }

    on<SlashCommandInteractionEvent> {
        // reactive programming / async handle
        // 1. sync: complete()
        // reply("").complete()
        // 2. async: queue()
        // reply("").queue()

        if (fullCommandName != "random lottery") {
            return@on
        }

        val name = getOptionAsStringWithLimitedLength(OPTION_NAME, 1..2 )


        reply("I am Random Lottery bot $name").queue()
    }
}
