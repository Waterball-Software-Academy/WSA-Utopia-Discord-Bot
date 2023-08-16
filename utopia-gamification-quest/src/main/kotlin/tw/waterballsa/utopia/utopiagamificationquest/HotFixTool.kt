package tw.waterballsa.utopia.utopiagamificationquest

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.State
import tw.waterballsa.utopia.utopiagamificationquest.listeners.UtopiaGamificationListener
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository

const val COMMAND_NAME = "hotfix"
const val FIND_COMMAND_NAME = "mission-log"
const val FIND_OPTION_NAME = "player"
const val CHECK_COMMAND_NAME = "check"

@Component
class HotFixTool(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val missionRepository: MissionRepository
) : UtopiaGamificationListener(guild, playerRepository) {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(COMMAND_NAME, "it is tools of fix quest system error")
                .addSubcommands(
                    SubcommandData(FIND_COMMAND_NAME, "find repository state")
                        .addOption(OptionType.USER, FIND_OPTION_NAME, "quest player", true),
                    SubcommandData(CHECK_COMMAND_NAME, "check mission state fail"),
                )
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            val commandInfo = fullCommandName.split(" ")
            if (commandInfo.first() != COMMAND_NAME) {
                return
            }

            when (commandInfo[1]) {
                FIND_COMMAND_NAME -> handleFindCommand()
                CHECK_COMMAND_NAME -> handleCheckCommand()
            }
        }
    }

    private fun SlashCommandInteractionEvent.handleFindCommand() {
        val user = getOption(FIND_OPTION_NAME)?.asUser ?: return

        deferReply().setEphemeral(true).queue()

        val missions = missionRepository.findAllByPlayerId(user.id)
        var result = """
            |${user.effectiveName} (${user.id})
            |--------------------------------------------
            |
        """.trimMargin()

        missions.ifEmpty {
            result += "not found\n"
        }

        missions.forEach {
            result += "${it.quest.title}(${it.quest.id}) : state -> ${it.state}\n"
        }

        result += "--------------------------------------------\n"

        hook.editOriginal(result).queue()
    }

    private fun SlashCommandInteractionEvent.handleCheckCommand() {
        deferReply().setEphemeral(true).queue()

        val isOk = mutableListOf<String>()
        val notOK = mutableListOf<String>()
        val workerRound = mutableListOf<String>()

        missionRepository.findAllByQuestId(10).forEach {
            isOk.add(it.player.id)
            workerRound.add(it.player.id)
        }

        (9 downTo 1).forEach {
            val missions = missionRepository.findAllByQuestId(it)

            missions.forEach { mission ->
                if (isOk.contains(mission.player.id).not() && notOK.contains(mission.player.id).not()) {
                    if (mission.state == State.IN_PROGRESS || mission.state == State.COMPLETED) {
                        isOk.add(mission.player.id)
                    } else {
                        notOK.add(mission.player.id)
                    }
                }
            }
        }

        val notOkUsers = notOK.map { jda.retrieveUserById(it).complete() }
        val users = workerRound.map { jda.retrieveUserById(it).complete() }

        hook.editOriginal("total count: ${notOK.size + isOk.size}\n" + notOkUsers.joinToString { it.effectiveName + '\n' } + "----------------\n" + users.joinToString { it.effectiveName + '\n' })
            .queue()

    }
}
