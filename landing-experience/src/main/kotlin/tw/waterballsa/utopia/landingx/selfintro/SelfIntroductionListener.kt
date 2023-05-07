package tw.waterballsa.utopia.landingx.selfintro

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

/**
 * Whenever a new self-introduction message sent in the self-introduction channel,
 *  should create a thread for that message
 */
@Component
class SelfIntroductionListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {
    private val log = KotlinLogging.logger {}

    override fun onMessageReceived(e: MessageReceivedEvent) {
        with(e) {
            if (wsa.selfIntroChannelId != e.messageId) {
                return
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
}
