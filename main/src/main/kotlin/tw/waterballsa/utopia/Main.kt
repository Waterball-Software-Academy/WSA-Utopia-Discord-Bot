package tw.waterballsa.utopia

import ch.qos.logback.core.util.OptionHelper.getEnv
import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.dsl.listeners
import tw.waterballsa.utopia.twosum.app.TwoSumUseCase

@OptIn(KordPreview::class)
fun main(args: Array<String>) {
    bot(getEnv("BOT_TOKEN")) {
        prefix { "/" } // ?
        configure {
            intents = Intents(Intent.GuildMessageReactions, Intent.DirectMessagesReactions)
        }
    }
}

fun logListeners() = listeners {
    on<MessageCreateEvent> {
        println(message.content)
    }
}
