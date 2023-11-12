package tw.waterballsa.utopia.managesubscriberole

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties

private val logger = KotlinLogging.logger {}

@Component
class RemoveSubscriberRoleListener(wsa : WsaDiscordProperties) : AbstractManageSubscriberRole(wsa) {

    /**
     * 新增表情，移除對應訂閱者身份組
     */
    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        manageSubscriberRole(event) { guild, user, role ->
            guild.removeRoleFromMember(user, role)
                    .queue {
                        logger.info { """[Remove Role] {"userId":"${user.id}", "roleName":"${role.name}" }""" }
                    }
        }
    }
}
