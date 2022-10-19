package tw.waterballsa.alpha.wsabot.gamification.listeners

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveAllEvent
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.fullName
import mu.KotlinLogging
import tw.waterballsa.alpha.wsabot.gamification.services.StatisticsService

fun statisticsListener(service: StatisticsService) = listeners {
    val logger = KotlinLogging.logger {}
    logger.trace { "registered" }

    on<ReactionAddEvent> {
        logger.trace { "ReactionAddEvent" }
        val author = message.asMessage().author!!
        val count = service.incrementReaction(author)
        val message = "${author.fullName} 目前總共獲得 $count 個表情。"
        channel.createMessage(message)
        val channelName = channel.asChannel().data.name.value
        logger.info { "Messaging to '$channelName': \"$message\"" }
    }

    on<ReactionRemoveAllEvent> {
        logger.trace { "ReactionRemoveAllEvent" }
    }
}