package tw.waterballsa.utopia.utopiagamification.quest.domain.actions

import mu.KotlinLogging
import tw.waterballsa.utopia.utopiagamification.quest.domain.Action
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

private val log = KotlinLogging.logger {}

class QuizAction(
    player: Player,
    val quizName: String,
    val correctCount: Int
) : Action(player) {

    override fun match(criteria: Criteria): Boolean = criteria is QuizCriteria
}

class QuizCriteria(
    private val quizName: String,
    val correctCount: Int,
    val totalCount: Int
) : Action.Criteria() {

    override fun meet(action: Action) = (action as? QuizAction)?.let { meetCriteria(it) } ?: false

    private fun meetCriteria(action: QuizAction): Boolean {
        log.info { """[quiz criteria] { "quizName" : "$quizName", "actionCorrectCount" : "${action.correctCount}", correctCount : "$correctCount", result : "${action.correctCount >= correctCount}" } """ }
        return action.quizName == quizName && action.correctCount >= correctCount
    }

    override fun toString(): String = "通過 $quizName 的考試，考試有 $totalCount 題，需答對 $correctCount 題以上。"
}
