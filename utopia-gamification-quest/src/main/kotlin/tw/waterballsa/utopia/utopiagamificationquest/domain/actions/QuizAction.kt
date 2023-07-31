package tw.waterballsa.utopia.utopiagamificationquest.domain.actions

import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

class QuizAction(
    player: Player,
    val quizName: String,
    val score: Int
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is QuizCriteria
}

class QuizCriteria(
    private val quizName: String,
    private val score: Int
) : Action.Criteria() {

    override fun meet(action: Action) = (action as? QuizAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: QuizAction): Boolean =
        action.quizName == quizName && action.score >= score
}
