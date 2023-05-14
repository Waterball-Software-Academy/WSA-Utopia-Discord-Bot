package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action

class MessageReactionAction(val messageId: String, val emoji: String) : Action() {

    override fun match(criteria: Criteria): Boolean = criteria is MessageReactionCriteria

    override fun updateProgress(criteria: Criteria) {
        when (criteria) {
            is MessageReactionCriteria -> {
                if (criteria.isSuffice(this)) {
                    criteria.complete()
                }
            }
        }
    }
}

class MessageReactionCriteria(
        private val unlockMessageId: String,
        private val unlockEmoji: String
) : Action.Criteria() {

    override fun isSuffice(action: Action): Boolean {
        return when (action) {
            is MessageReactionAction -> action.messageId == unlockMessageId && action.emoji == unlockEmoji
            else -> false
        }
    }
}
