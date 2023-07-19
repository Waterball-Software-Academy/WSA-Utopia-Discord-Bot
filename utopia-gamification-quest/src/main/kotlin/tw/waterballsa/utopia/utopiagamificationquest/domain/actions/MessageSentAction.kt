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
    private val channelId: String,
    goalCount: Int,
    private val hasReplied: Boolean = false,
    private val hasImage: Boolean = false,
    private val numberOfVoiceChannelMembers: Int = 0,
    private val regex: Regex = ".*".toRegex(),
) : Criteria(goalCount) {

    override fun meetAction(action: Action) =
        (action as? MessageSentAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: MessageSentAction): Boolean =
        action.channelId == channelId
                && action.numberOfVoiceChannelMembers >= numberOfVoiceChannelMembers
                && action.hasReplied == hasReplied
                && action.hasImage == hasImage
                && action.context matches regex
}

