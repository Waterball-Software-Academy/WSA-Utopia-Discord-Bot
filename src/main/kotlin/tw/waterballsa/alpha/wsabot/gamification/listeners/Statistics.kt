package tw.waterballsa.alpha.wsabot.gamification.listeners

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveAllEvent
import dev.kord.core.event.message.ReactionRemoveEmojiEvent
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging

fun statisticsListener() = listeners {
    val logger = KotlinLogging.logger {}
    logger.trace("registered")

    on<ReactionAddEvent> {
        logger.trace("notify")
        val author = message.asMessage().author

    }
    on<ReactionRemoveEmojiEvent> {
        logger.trace("notify")
    }
    on<ReactionRemoveAllEvent> {
        logger.trace("notify")
    }
}