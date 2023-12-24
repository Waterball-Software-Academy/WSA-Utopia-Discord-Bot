package tw.waterballsa.utopia.managesubscriberole

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties

private val logger = KotlinLogging.logger {}

@Component
class AddSubscriberRoleListener(wsa: WsaDiscordProperties) : AbstractManageSubscriberRole(wsa) {

    /**
     * 移除表情時，新增對應訂閱者身份組
     */
    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        with(event) {
            manageSubscriberRole(guild::addRoleToMember)?.queue {
                logger.info { "[Add Role] {\"userId\":\"${userId}\", \"roleName\":\"${it.name}\" }" }
            }
        }
    }
}
