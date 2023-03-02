package tw.waterballsa.alpha.wsabot.bot.app

import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging

fun autoThreadListener() = listeners {
    val logger = KotlinLogging.logger {}

    on<MessageCreateEvent> {
        val channelIdValue = message.channelId.value
        val betaSelfIntroductionChannelId = 1039196068455399474u
        val prodSelfIntroductionChannelId = 937992281837961257u
        if (!listOf(betaSelfIntroductionChannelId, prodSelfIntroductionChannelId).contains(channelIdValue)) {
            return@on
        }
        val author = message.asMessage().author!!
        val threadName = "【${author.username}】"
        discord.kord.rest.channel.startThreadWithMessage(
            message.channelId,
            message.id,
            name = threadName,
            ArchiveDuration.Week
        ) {}
        logger.info { "Thread $threadName created" }
    }
}
