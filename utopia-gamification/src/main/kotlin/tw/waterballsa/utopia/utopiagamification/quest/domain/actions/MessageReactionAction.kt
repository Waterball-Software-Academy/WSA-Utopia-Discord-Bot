package tw.waterballsa.utopia.utopiagamification.quest.domain.actions

import tw.waterballsa.utopia.utopiagamification.quest.domain.Action
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

class MessageReactionAction(
    player: Player,
    val messageId: String,
    val emoji: String
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is MessageReactionCriteria
}

class MessageReactionCriteria(
    private val channelIdRule: ChannelIdRule,
    private val messageId: String,
    private val emoji: String,
) : Action.Criteria() {

    override fun meet(action: Action) =
        (action as? MessageReactionAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: MessageReactionAction): Boolean =
        action.messageId == messageId && action.emoji == emoji

    override fun toString(): String = "點選 $emoji 表情。"
}
