package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Criteria
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import kotlin.reflect.safeCast


class MessageSentAction(
    player: Player,
    val channelId: String,
    val context: String,
    val hasReplied: Boolean,
    val hasImage: Boolean,
    val numberOfVoiceChannelMembers: Int,
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is MessageSentCriteria

}

class MessageSentCriteria(
    // 依照需求會有 "任意頻道" 和 "指定頻道" 的情況
    private val channelIdRule: ChannelIdRule,
    // 依照需求會有 "忽略此規則" 和 "訊息一定要回覆" 和 "訊息一定沒有回覆" 的情況
    private val hasRepliedRule: HasRule = HasRule.IGNORE,
    // 依照需求會有 "忽略此規則" 和 "訊息一定要有圖片"，和 "訊息一定沒有圖片" 的情況
    private val hasImageRule: HasRule = HasRule.IGNORE,
    // 依照需求會有 "使用regex匹配指定的訊息內容" 和 "忽略regex匹配指定的訊息內容" 的情況
    private val regexRule: RegexRule = RegexRule.IGNORE,
    private val numberOfVoiceChannelMembersRule: AtLeastRule = AtLeastRule.IGNORE,
) : Action.Criteria() {
    private val rules = listOf(channelIdRule, hasRepliedRule, hasImageRule, regexRule, numberOfVoiceChannelMembersRule)

    override fun meet(action: Action) =
        (action as? MessageSentAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: MessageSentAction): Boolean =
        channelIdRule.meet(action.channelId)
                && hasRepliedRule.meet(action.hasReplied)
                && hasImageRule.meet(action.hasImage)
                && regexRule.meet(action.context)
                && numberOfVoiceChannelMembersRule.meet(action.numberOfVoiceChannelMembers)
}

class ChannelIdRule(private val channelId: String) {
    companion object {
        val ANY_CHANNEL = ChannelIdRule("")
    }

    fun meet(actionChannelId: String): Boolean = actionChannelId.contains(channelId)
}

enum class HasRule(private val hasObject: Boolean?) {
    IGNORE(null),
    TRUE(true),
    FALSE(false);

    fun meet(actionHasObject: Boolean): Boolean = hasObject?.let { it == actionHasObject } ?: true
}

class RegexRule(private val regex: Regex) {
    companion object {
        val IGNORE = RegexRule(".*".toRegex())
    }

    fun meet(context: String): Boolean = context matches regex
}

class AtLeastRule(
    private val number: Int
) {
    companion object {
        val IGNORE = AtLeastRule(0)
    }

    fun meet(count: Int): Boolean = count >= number
}

