package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import kotlin.reflect.safeCast

class MessageReactionAction(
        player: Player,
        val messageId: String,
        val emoji: String
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is MessageReactionCriteria

}

class MessageReactionCriteria(
        private val unlockMessageId: String,
        private val unlockEmoji: String
) : Action.Criteria() {

    override fun meet(action: Action) = MessageReactionAction::class.safeCast(action)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: MessageReactionAction): Boolean = action.messageId == unlockMessageId && action.emoji == unlockEmoji
}
