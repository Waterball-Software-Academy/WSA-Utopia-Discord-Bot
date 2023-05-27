package tw.waterballsa.utopia.document.extensions

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.Command.Subcommand
import net.dv8tion.jda.api.interactions.commands.ICommandReference
import tw.waterballsa.utopia.document.domain.CommandDocument
import tw.waterballsa.utopia.document.domain.CommandInfo
import tw.waterballsa.utopia.document.domain.CommandOption

fun List<Command>.toCommandDocument(): CommandDocument {
    val commandInfos = flatMap { command ->
        val commandList = command.subcommands.takeUnless { it.isEmpty() } ?: listOf(command)
        commandList.map { it.toCommandInfo() }
    }
    return CommandDocument(commandInfos)
}

private fun ICommandReference.toCommandInfo(): CommandInfo {
    return when (this) {
        is Command -> toCommandInfo()
        is Subcommand -> toCommandInfo()
        else -> throw IllegalArgumentException("Unsupported CommandReference type")
    }
}

private fun Command.toCommandInfo(): CommandInfo {
    return CommandInfo(
            name,
            fullCommandName,
            description,
            options.toCommandOptions()
    )
}

private fun Subcommand.toCommandInfo(): CommandInfo {
    return CommandInfo(
            getParentCommandName(),
            fullCommandName,
            description,
            options.toCommandOptions()
    )
}

private fun Subcommand.getParentCommandName(): String {
    return fullCommandName.removeSuffix(" $name")
}

private fun List<Command.Option>.toCommandOptions(): List<CommandOption> {
    return map { CommandOption(it.name, it.type.name, it.description) }
}
