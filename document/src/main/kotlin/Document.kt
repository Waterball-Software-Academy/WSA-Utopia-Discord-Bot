import extensions.generateCommandTableMarkdown
import net.dv8tion.jda.api.entities.Guild
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import tw.waterballsa.utopia.jda.WSA_GUILD_BEAN_NAME

fun generateCommandTableMarkdown(context: AnnotationConfigApplicationContext,
                                 filePath: String) {
    context.getBean(WSA_GUILD_BEAN_NAME, Guild::class.java)
            .generateCommandTableMarkdown(filePath)
}
