package extensions

import net.dv8tion.jda.api.entities.Guild


fun Guild.generateCommandTableMarkdown(): String {
    return retrieveCommands()
            .complete()
            .toCommandDocument()
            .buildCommandTableMarkdown()
}
