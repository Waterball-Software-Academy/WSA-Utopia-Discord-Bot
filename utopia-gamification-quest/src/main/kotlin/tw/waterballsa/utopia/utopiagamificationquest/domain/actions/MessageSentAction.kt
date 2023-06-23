package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

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
        private val channelId: String,
        private val goalCount: Int,
        private val hasReplied: Boolean = false,
        private val hasImage: Boolean = false,
        private val numberOfVoiceChannelMembers: Int = 0,
        private val regex: Regex = ".*".toRegex(),
        private var completedTimes: Int = 0
) : Action.Criteria() {

    override fun isFulfilled(action: Action): Boolean {
        return when (action) {
            is MessageSentAction -> action.channelId.contains(channelId) &&
                    action.numberOfVoiceChannelMembers >= numberOfVoiceChannelMembers &&
                    action.hasReplied == hasReplied &&
                    action.hasImage == hasImage &&
                    action.context matches regex &&
                    ++completedTimes >= goalCount

            else -> false
        }
    }
}
