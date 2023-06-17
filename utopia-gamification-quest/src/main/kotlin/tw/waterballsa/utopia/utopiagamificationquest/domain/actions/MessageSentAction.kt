package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action

class MessageSentAction(
        val channelId: String,
        val context: String
) : Action() {

    override fun match(criteria: Criteria): Boolean = criteria is MessageSentCriteria

}

class MessageSentCriteria(
        private val channelId: String,
        private val discussionCount: Int = 1,
        private val regex: Regex = ".*".toRegex(),
        private var count: Int = 0,
) : Action.Criteria() {
    override fun isFulfilled(action: Action): Boolean {
        return when (action) {
            is MessageSentAction -> action.channelId == channelId && ++count >= discussionCount && action.context matches regex
            else -> false
        }
    }
}
