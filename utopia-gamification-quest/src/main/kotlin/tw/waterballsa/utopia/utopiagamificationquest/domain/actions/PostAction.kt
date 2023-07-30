package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

class PostAction(
    player: Player,
    val channelId: String
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is PostCriteria
}

class PostCriteria(
    private val channelId: String
) : Action.Criteria() {

    override fun meet(action: Action) = (action as? PostAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: PostAction): Boolean = action.channelId == channelId
}
