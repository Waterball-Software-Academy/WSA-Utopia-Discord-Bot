package tw.waterballsa.alpha.wsabot

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.*
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.dsl.listeners
import java.util.*

// 1. declare commands
// 2. listen to events
// 3. control bot
@OptIn(PrivilegedIntent::class)
fun main(args: Array<String>) {
    bot(getBotToken()) {
        prefix { "+" } // ?
        configure {
            intents = Intents(
                Intent.GuildMessageReactions,
                Intent.DirectMessagesReactions,
                Intent.GuildScheduledEvents,
                Intent.GuildVoiceStates,
                Intent.GuildMembers,
                Intent.Guilds,
            )
            defaultPermissions = Permissions(
                Permission.All,
                Permission.ViewGuildInsights,
                Permission.ViewChannel,
                Permission.ManageChannels
            )
        }

    }
}

fun getBotToken(): String {
    val properties = Properties()
    val file = Thread.currentThread().contextClassLoader.getResourceAsStream("secret.properties")
    properties.load(file)
    return properties.getProperty("token")
}

fun logListeners() = listeners {
    on<MessageCreateEvent> {
        if (!(message.author?.isBot!!))
            println(message.content)
    }
}