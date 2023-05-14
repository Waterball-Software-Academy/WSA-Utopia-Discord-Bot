package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action

class MessageSentAction(
        val channelId: String,
        val context: String
) : Action() {

    override fun match(criteria: Criteria): Boolean = criteria is MessageSentCriteria

    override fun updateProgress(criteria: Criteria) {
        when (criteria) {
            is MessageSentCriteria -> {
                criteria.addTimes(1)
                if (criteria.isSuffice(this)) {
                    criteria.complete()
                }
            }
        }
    }
}

class MessageSentCriteria(
        private val channelId: String,
        private val discussionTimes: Int = 1,
        private val regex: Regex = ".*".toRegex(),
        private var times: Int = 0,
) : Action.Criteria() {

    fun addTimes(times: Int) {
        this.times += times
    }

    override fun isSuffice(action: Action): Boolean {
        return when (action) {
            is MessageSentAction -> action.channelId == channelId && times >= discussionTimes && action.context matches regex
            else -> false
        }

    }
}
