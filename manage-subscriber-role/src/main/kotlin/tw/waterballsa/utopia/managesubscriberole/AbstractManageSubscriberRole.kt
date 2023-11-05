package tw.waterballsa.utopia.managesubscriberole

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

abstract class AbstractManageSubscriberRole(private val wsa: WsaDiscordProperties): UtopiaListener() {

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
     * 管理訂閱者身份組
     */
    protected fun <T: GenericMessageReactionEvent> manageSubscriberRole(event: T,
                                                                        handleSubscriberRole: (guild: Guild, userSnowflake: UserSnowflake, role: Role) -> Unit) {
        with(event) {
            if (messageId != wsa.manageSubscriberRoleMessageId) {
                return
            }
            emojiToSubscriberRoleIdMap[emoji.name]?.run {
                val user = UserSnowflake.fromId(userId)
                val role = jda.getRoleById(this)?: return
                handleSubscriberRole(guild, user, role)
            }
        }
    }
}
