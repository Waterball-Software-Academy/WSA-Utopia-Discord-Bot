package tw.waterballsa.utopia.unsubscriberole

import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener


@Component
class UnsubscribeRoleListener(val wsa: WsaDiscordProperties): UtopiaListener() {

    private val emojiIdToRoleId = hashMapOf<String, String>()

    /**
     *                          prod                     beta
     * "1️⃣"：軟體英文派對訂閱者    1056758845264900166      1095747365215932487
     * "2️⃣"：遊戲微服務計畫訂閱者  1042774972717871176      1038661611889631262
     * "3️⃣"：純函式咖啡訂閱者     1038933719723020318      1095747473345097749
     * "4️⃣"：技術演講吐司會訂閱者  1042775110630780958      1038661795507863623
     * "7️⃣"：CS Lab 訂閱者       1051031301609758752      1095747425194483762
     */
    init {
        emojiIdToRoleId[wsa.wsaGaaSMemberRoleId] = "2️⃣"
    }

    override fun onEmojiAdded(event: EmojiAddedEvent) {
        super.onEmojiAdded(event)
    }


}
