package tw.waterballsa.utopia.random

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithLimitedLength
import tw.waterballsa.utopia.jda.listener


fun randomDice(wsa: WsaDiscordProperties) = listener {
    // channelId

    // [ /command ]
    // [ /<feature-module's name> <feature's name> <param1> <param2> <param3> ... ]
    // [ /random lottery number ]
    command {
        Commands.slash("dice", "Get a dice number.")
            .addSubcommands(
                SubcommandData("number", "get a dice number or input max value.")
                    .addOption(OptionType.INTEGER, "number", "Number of a Max number.", false)
            )
    }

    on<SlashCommandInteractionEvent> {
        // reactive programming / async handle
        // 1. sync: complete()
        // reply("").complete()
        // 2. async: queue()
        // reply("").queue()

        if (fullCommandName != "dice number") {
            return@on
        }
        val max = getOptionAsPositiveInt("number") ?: 6
        val number = (1..max).random()

        reply("your dice number is $number.").queue()
    }
}
