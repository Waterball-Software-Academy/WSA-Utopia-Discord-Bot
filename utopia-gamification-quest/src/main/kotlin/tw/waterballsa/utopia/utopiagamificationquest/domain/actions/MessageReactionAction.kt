package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

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

    override fun isFulfilled(action: Action): Boolean {
        return when (action) {
            is MessageReactionAction -> action.messageId == unlockMessageId && action.emoji == unlockEmoji
            else -> false
        }
    }
}
