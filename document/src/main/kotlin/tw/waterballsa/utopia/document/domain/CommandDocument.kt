package tw.waterballsa.utopia.document.domain

import mu.KotlinLogging
import net.steppschuh.markdowngenerator.table.Table

const val HEADER_COMMANDS = "Commands"
const val ARGUMENTS = "Arguments"
const val DESCRIPTION = "Description"

private val log = KotlinLogging.logger {}

class CommandDocument(
        private val commandInfos: List<CommandInfo> = emptyList()
) {
    fun buildCommandTableMarkdown(): String = """
        ># Commands Document
        >
        >${composeCommandsTable()}
        """.trimMargin(">")

    private fun composeCommandsTable(): String =
            commandInfos.groupBy { it.parentCommandName }
                    .map { (parentCommandName, commands) ->
                        log.debug { "[GenerateCommandFile] {\"command\":\"$parentCommandName\"}" }
                        """
                        >## $parentCommandName
                        >${commands.buildCommandsTable()}
                        """.trimMargin(">")
                    }.joinToString("\n\n")

    private fun List<CommandInfo>.buildCommandsTable(): String {
        val table = Table.Builder()
                .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_COMMANDS, ARGUMENTS, DESCRIPTION)
        forEach { table.addRow(it.toTableRow()) }
        return table.build().toString()
    }
}
