package tw.waterballsa.utopia.managesubscriberole

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

private val logger = KotlinLogging.logger {}
@Component
class ManageSubscribeRoleListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {

    private val emojiToSubscriberRoleIdMap = hashMapOf<String, String>()

    /**
     * "1️⃣"：軟體英文派對訂閱者
     * "2️⃣"：遊戲微服務計畫訂閱者
     * "3️⃣"：純函式咖啡訂閱者
     * "4️⃣"：技術演講吐司會訂閱者
     * "7️⃣"：CS Lab 訂閱者
     */
    init {
        emojiToSubscriberRoleIdMap["1️⃣"] = wsa.wsaSECSubsriberMemberRoleId
        emojiToSubscriberRoleIdMap["2️⃣"] = wsa.wsaGaaSSubscriberRoleId
        emojiToSubscriberRoleIdMap["3️⃣"] = wsa.wsaPurefuncSubscriberRoleId
        emojiToSubscriberRoleIdMap["4️⃣"] = wsa.wsaTTMSubscriberRoleId
        emojiToSubscriberRoleIdMap["7️⃣"] = wsa.wsaCSLabSubscriberRoleId
    }

    /**
     * 新增表情，移除對應訂閱者身份組
     */
    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with(event) {
            if (messageId != wsa.manageSubscriberRoleMessageId) {
                return
            }
            emojiToSubscriberRoleIdMap[emoji.name]?.run {
                val user = UserSnowflake.fromId(userId)
                val role = jda.getRoleById(this)?: return
                guild.removeRoleFromMember(user, role)
                        .queue {
                            logger.info { "[Remove Role] {\"userId\":\"${user.id}\", \"roleName\":\"${role.name}\" }" }
                        }
            }
        }
    }

    /**
     * 移除表情，新增對應訂閱者身份組
     */
    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        with(event) {
            if (messageId != wsa.manageSubscriberRoleMessageId) {
                return
            }
            emojiToSubscriberRoleIdMap[emoji.name]?.run {
                val user = UserSnowflake.fromId(userId)
                val role = jda.getRoleById(this)?: return
                guild.addRoleToMember(user, role)
                        .queue {
                            logger.info { "[Add Role] {\"userId\":\"${user.id}\", \"roleName\":\"${role.name}\" }" }
                        }
            }
        }
    }

}
