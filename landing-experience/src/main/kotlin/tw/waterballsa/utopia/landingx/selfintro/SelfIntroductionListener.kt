package tw.waterballsa.utopia.landingx.selfintro

import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties

val log = KotlinLogging.logger {}

fun autoCreateThreadWheneverMemberCreateMessageInSelfIntroChannel(wsa: WsaDiscordProperties) = listeners {
    on<MessageCreateEvent> {
        val channelIdValue = message.channelId.value
        if (wsa.selfIntroChannelId != channelIdValue) {
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
        log.info { "[Auto create a thread on new message] {\"threadName\":\"$threadName\"}" }
    }
}
