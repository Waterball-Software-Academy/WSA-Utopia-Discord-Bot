package tw.waterballsa.utopia.utopiagamification

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.utopiagamification.quest.domain.State
import tw.waterballsa.utopia.utopiagamification.quest.listeners.UtopiaGamificationListener
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

const val COMMAND_NAME = "hotfix"
const val FIND_COMMAND_NAME = "mission-log"
const val FIND_OPTION_NAME = "player"
const val CHECK_COMMAND_NAME = "check"

//只能使用在 local 端。
//@Component
class HotFixTool(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val missionRepository: MissionRepository
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(COMMAND_NAME, "it is tools of fix quest system error")
            .addSubcommands(
                SubcommandData(FIND_COMMAND_NAME, "find repository state")
                    .addOption(OptionType.USER, FIND_OPTION_NAME, "quest player", true),
                SubcommandData(CHECK_COMMAND_NAME, "check mission state fail"),
            )
    )

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
            result += "${it.quest.title}(${it.quest.id}) : state -> ${it.state}, date -> ${it.completedTime}\n"
        }

        result += "--------------------------------------------\n"

        hook.editOriginal(result).queue()
    }

    private fun SlashCommandInteractionEvent.handleCheckCommand() {
        deferReply().setEphemeral(true).queue()

        val isOk = mutableListOf<String>()
        val notOK = mutableListOf<String>()
        val workerRound = mutableListOf<String>()
        val questProgressRate = mutableMapOf<Int, MutableList<String>>()

        (10 downTo 1).forEach {
            questProgressRate[it] = mutableListOf()
        }

        missionRepository.findAllByQuestId(10).forEach {
            isOk.add(it.player.id)
            workerRound.add(it.player.id)
            if (it.state == State.COMPLETED) {
                questProgressRate.getOrDefault(10, mutableListOf()).add(it.player.id)
            }
        }

        (9 downTo 1).forEach {
            val missions = missionRepository.findAllByQuestId(it)

            missions.forEach { mission ->
                if (mission.state == State.COMPLETED) {
                    questProgressRate.getOrDefault(it, mutableListOf()).add(mission.player.id)
                }
                if (isOk.contains(mission.player.id).not() && notOK.contains(mission.player.id).not()) {
                    if (mission.state == State.IN_PROGRESS || mission.state == State.COMPLETED) {
                        isOk.add(mission.player.id)
                    } else {
                        notOK.add(mission.player.id)
                    }
                }
            }
        }

        val rank = questProgressRate.map {
            it.key to it.value.map { id ->
                playerRepository.findPlayerById(id)?.name ?: id
            }
        }

        hook.editOriginal(
            """
            not ok count: ${notOK.size}
            is ok count: ${isOk.size}
            --------------------------------------------------
            $rank
            --------------------------------------------------
            """.trimIndent()
        ).queue()
    }
}
