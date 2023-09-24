package tw.waterballsa.utopia.gamification.quest.domain.actions

import tw.waterballsa.utopia.gamification.quest.domain.Action
import tw.waterballsa.utopia.gamification.quest.domain.Player

class JoinActivityAction(
    player: Player,
    val eventName: String,
    val maxMemberCount: Int,
    val stayDuration: Int,
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is JoinActivityCriteria
}

class JoinActivityCriteria(
    private val eventName: String,
    private val maxMemberCount: Int,
    private val eventDuration: Int
) : Action.Criteria() {

    override fun meet(action: Action): Boolean = (action as? JoinActivityAction)?.let { meetCriteria(action) } ?: false

    private fun meetCriteria(action: JoinActivityAction): Boolean {
        return action.maxMemberCount >= maxMemberCount
                && action.stayDuration >= eventDuration
                && action.eventName.contains(eventName)
    }

    override fun toString(): String = "參與 $eventName 活動，至少待滿 $eventDuration 分鐘後離開活動。"
}
