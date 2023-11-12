package tw.waterballsa.utopia.utopiagamification.quest.domain.actions

import tw.waterballsa.utopia.utopiagamification.quest.domain.Action

class PostAction(
    playerId: String,
    val channelId: String
) : Action(playerId) {

    override fun match(criteria: Criteria): Boolean = criteria is PostCriteria
}

class PostCriteria(
    private val channelIdRule: ChannelIdRule
) : Action.Criteria() {

    override fun meet(action: Action) = (action as? PostAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: PostAction): Boolean = channelIdRule.meet(action.channelId)

    override fun toString(): String = "發一則貼文"
}
