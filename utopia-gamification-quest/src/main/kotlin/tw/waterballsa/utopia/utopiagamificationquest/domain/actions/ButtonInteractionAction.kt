package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

class ButtonInteractionAction(
    player: Player,
    val buttonName: String
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is ButtonInteractionCriteria
}

class ButtonInteractionCriteria(
    private val buttonName: String
) : Action.Criteria() {

    override fun meet(action: Action): Boolean =
        (action as? ButtonInteractionAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: ButtonInteractionAction): Boolean = false
}
