package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

class MessageSentAction(
        player: Player,
        val channelId: String,
        val context: String,
        val isReplied: Boolean,
        val containsImage: Boolean,
        val voicePopulation: Int,
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is MessageSentCriteria

}

class MessageSentCriteria(
        private val channelId: String,
        private val discussionCount: Int,
        private val isReplied: Boolean = false,
        private val containsImage: Boolean = false,
        private val voicePopulation: Int = 0,
        private val regex: Regex = ".*".toRegex(),
        private var count: Int = 0
) : Action.Criteria() {

    override fun isFulfilled(action: Action): Boolean {
        return when (action) {
            is MessageSentAction -> action.channelId.contains(channelId) && action.voicePopulation >= voicePopulation && action.isReplied == isReplied && action.containsImage == containsImage && action.context matches regex && ++count >= discussionCount
            else -> false
        }
    }
}
