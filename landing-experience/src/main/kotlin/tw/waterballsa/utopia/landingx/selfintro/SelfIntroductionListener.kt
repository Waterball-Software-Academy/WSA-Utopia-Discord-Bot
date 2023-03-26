package tw.waterballsa.utopia.landingx.selfintro

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener

val log = KotlinLogging.logger {}

fun autoCreateThreadWheneverMemberCreateMessageInSelfIntroChannel(wsa: WsaDiscordProperties) = listener {
    on<MessageReceivedEvent> {
        val channelIdValue = message.channel.id
        if (wsa.selfIntroChannelId != channelIdValue) {
            return@on
        }
        val author = message.author
        val threadName = "【${author.name}】"

        message.startedThread ?: let {
            message.createThreadChannel(threadName).queue {
                log.info { "[Auto create a thread on new message] {\"threadName\":\"${message.startedThread!!.name}\"}" }
            }
        }
    }
}
