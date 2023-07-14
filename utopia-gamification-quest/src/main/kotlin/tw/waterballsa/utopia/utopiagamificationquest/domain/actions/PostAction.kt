package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import kotlin.reflect.safeCast


class PostAction(player: Player, val channelId: String) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is PostCriteria

}

class PostCriteria(
    private val channelId: String,
    postTimes: Int = 1
) : Action.Criteria(postTimes) {

    override fun meetAction(action: Action) = PostAction::class.safeCast(action)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: PostAction): Boolean = action.channelId == channelId
}
