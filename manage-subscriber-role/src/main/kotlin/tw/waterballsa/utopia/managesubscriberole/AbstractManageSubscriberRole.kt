package tw.waterballsa.utopia.managesubscriberole

import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.requests.RestAction
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

abstract class AbstractManageSubscriberRole(private val wsa: WsaDiscordProperties) : UtopiaListener() {

    /**
     * "1️⃣"：軟體英文派對訂閱者
     * "2️⃣"：遊戲微服務計畫訂閱者
     * "3️⃣"：純函式咖啡訂閱者
     * "4️⃣"：技術演講吐司會訂閱者
     * "7️⃣"：CS Lab 訂閱者
     */
    private val emojiToSubscriberRoleId = hashMapOf(
            "1️⃣" to wsa.wsaSECSubsriberMemberRoleId,
            "2️⃣" to wsa.wsaGaaSSubscriberRoleId,
            "3️⃣" to wsa.wsaPurefuncSubscriberRoleId,
            "4️⃣" to wsa.wsaTTMSubscriberRoleId,
            "7️⃣" to wsa.wsaCSLabSubscriberRoleId
    )

    /**
     * 管理訂閱者身份組
     */
    protected fun GenericMessageReactionEvent.manageSubscriberRole(handleSubscriberRole: (user: UserSnowflake, role: Role) -> RestAction<Void>): RestAction<Role>? {
        if (messageId != wsa.manageSubscriberRoleMessageId) {
            return null
        }
        val user = User.fromId(userId)
        val role = role ?: return null
        return handleSubscriberRole(user, role).map { role }
    }


    private val GenericMessageReactionEvent.role: Role?
        get() = emojiToSubscriberRoleId[emoji.name]?.let { jda.getRoleById(it) }
}
