package extensions

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand
import net.dv8tion.jda.api.interactions.commands.ICommandReference
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.table.TableRow
import tw.waterballsa.utopia.jda.log
import java.io.File
import java.io.IOException

const val HEADER_COMMANDS = "Commands"
const val ARGUMENTS = "Arguments"
const val DESCRIPTION = "Description"

private data class CommandInfo(val fullCommandName: String, val optionNames: String, val description: String)

fun Guild.generateCommandTableMarkdown(filePath: String) {
    val file = File(filePath)

    try {
        file.bufferedWriter().use { writer ->
            writer.write("# Commands Document\n\n")
            retrieveCommands().complete().forEach { command ->
                log.debug { "[GenerateCommandFile] {\"command\":\"${command.name}\"}" }
                writer.append(command.generateCommandTableMarkdown())
            }
        }
    } catch (e: IOException) {
        log.warn { "[Error during generateCommandFile] {\"message\":\"${e.message}\"" }
    }
}

private fun Command.generateCommandTableMarkdown(): String {
    val table = Table.Builder()
            .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER)
            .addRow(HEADER_COMMANDS, ARGUMENTS, DESCRIPTION)

    val commandList = if (hasSubCommand()) listOf(this) else subcommands

    commandList.map { it.convertToCommandInfo() }
            .forEach { table.addRow(it.toTableRow()) }

    return """
        >## $name
        >${table.build()}
        >
        >
    """.trimMargin(">")
}

private fun Command.hasSubCommand(): Boolean = subcommands.isEmpty()

private fun ICommandReference.convertToCommandInfo(): CommandInfo {
    return when (this) {
        is Command -> CommandInfo(fullCommandName, options.getOptionNamesAsString(), description)
        is Subcommand -> CommandInfo(fullCommandName, options.getOptionNamesAsString(), description)
        else -> throw IllegalArgumentException("Unsupported CommandReference type")
    }
}

private fun List<Command.Option>.getOptionNamesAsString(): String =
        joinToString(" ") { it.name }

private fun CommandInfo.toTableRow(): TableRow<String> =
        TableRow(listOf(fullCommandName, optionNames, description))
