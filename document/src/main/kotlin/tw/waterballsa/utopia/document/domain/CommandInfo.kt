package tw.waterballsa.utopia.document.domain

import net.steppschuh.markdowngenerator.table.TableRow

data class CommandInfo(
        val parentCommandName: String,
        val fullCommandName: String,
        val description: String,
        val options: List<CommandOption> = emptyList()
) {
    fun toTableRow(): TableRow<String> =
            TableRow(
                    listOf(
                            fullCommandName,
                            composeOptionsDocument(),
                            description
                    )
            )

    private fun composeOptionsDocument(): String =
            options.joinToString("<br>") { it.toDocument() }
}
