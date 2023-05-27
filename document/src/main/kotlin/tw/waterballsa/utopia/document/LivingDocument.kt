package tw.waterballsa.utopia.document

import net.dv8tion.jda.api.entities.Guild
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import tw.waterballsa.utopia.document.extensions.generateCommandTableMarkdown
import tw.waterballsa.utopia.jda.WSA_GUILD_BEAN_NAME
import tw.waterballsa.utopia.jda.log
import java.io.File
import java.io.IOException

fun generateCommandTableMarkdown(context: AnnotationConfigApplicationContext, filePath: String) {
    val document = context.getBean(WSA_GUILD_BEAN_NAME, Guild::class.java).generateCommandTableMarkdown()
    val file = File(filePath)

    try {
        file.bufferedWriter().use { writer ->
            writer.write(document)
        }
    } catch (e: IOException) {
        log.warn { "[Error during generateCommandFile] {\"message\":\"${e.message}\"" }
    }
}
