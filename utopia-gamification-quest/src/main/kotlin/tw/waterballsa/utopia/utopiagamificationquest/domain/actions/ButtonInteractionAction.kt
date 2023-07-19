package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.QuizButton
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton

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

    //TODO 之後開始出現重複的程式碼，再重構
    private fun meetCriteria(action: ButtonInteractionAction): Boolean {
        if (action.buttonName == buttonName && buttonName == QuizButton.NAME) {
            return handleQuizButton(action)
        }

        return false
    }

    //TODO 之後要加入考試邏輯
    private fun handleQuizButton(action: ButtonInteractionAction): Boolean {
        return true
    }
}
