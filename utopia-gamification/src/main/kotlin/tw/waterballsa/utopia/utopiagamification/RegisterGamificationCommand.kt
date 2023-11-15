package tw.waterballsa.utopia.utopiagamification

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType.STRING
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.extensions.addOptionalOption
import tw.waterballsa.utopia.utopiagamification.quest.listeners.UtopiaGamificationListener
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

const val UTOPIA_COMMAND_NAME = "utopia"
const val FIRST_QUEST_COMMAND_NAME = "first-quest"
const val REVIEW_COMMAND_NAME = "re-render"
const val OPTION_COMMAND_NAME = "options"

private const val LEADERBOARD_COMMAND_NAME = "leaderboard"
private const val LEADERBOARD_OPTION_MY_RANK = "my-rank"

@Component
class RegisterGamificationCommand(
    guild : Guild,
    playerRepository: PlayerRepository
) : UtopiaGamificationListener(guild, playerRepository){
    override fun commands(): List<CommandData> = listOf(
        Commands.slash(UTOPIA_COMMAND_NAME, "utopia command")
            .addSubcommands(
                // 任務系統
                SubcommandData(FIRST_QUEST_COMMAND_NAME, "get first quest"),
                // 查詢並重發任務
                SubcommandData(REVIEW_COMMAND_NAME, "re-render in_progress/completed quest"),
                // 排行榜
                SubcommandData(LEADERBOARD_COMMAND_NAME, "leaderboard")
                    .addOptionalOption(
                        STRING,
                        OPTION_COMMAND_NAME,
                        LEADERBOARD_OPTION_MY_RANK,
                        Command.Choice(LEADERBOARD_OPTION_MY_RANK, LEADERBOARD_OPTION_MY_RANK)
                    )
            )
    )
}
