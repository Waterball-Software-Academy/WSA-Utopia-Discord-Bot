package tw.waterballsa.utopia.knowledgeking.domain

import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class KnowledgeKing(private val quiz: Quiz, private val timeBetweenQuestionsInSeconds: Long) {
    val id: String = UUID.randomUUID().toString()
    private val questionIterator = quiz.questions.iterator()
    var currentQuestion: Question? = null
    private val scoreboard = Scoreboard()
    private var gameStarted = false
    private var gameOver = false
    private var currentQuestionStartTimeInMillis: Long = 0

    fun startContest(): List<Event> {
        gameStarted = true
        currentQuestion = nextQuestion()?.question

        return listOf(
            ContestStartedEvent(quiz.questions.size),
            NextQuestionEvent(1, this.currentQuestion!!, false)
        )
    }

    fun answer(contestantId: String?, answer: Answer): AnsweredEvent {
        if (gameOver) {
            throw IllegalStateException("The game is over, cannot answer the question.")
        }
        return if (currentQuestion?.isCorrectAnswer(answer) == true) {
            val secondsElapsed = (currentTimeMillis() - currentQuestionStartTimeInMillis).milliseconds.inWholeSeconds
            scoreboard.win(contestantId!!, 500 * secondsElapsed / timeBetweenQuestionsInSeconds)
            AnsweredEvent(answer, contestantId, AnswerResult.CORRECT)
        } else {
            AnsweredEvent(answer, contestantId!!, AnswerResult.WRONG)
        }
    }

    fun nextQuestion(): NextQuestionEvent? {
        if (gameOver || !gameStarted || !questionIterator.hasNext()) {
            return null
        }
        currentQuestionStartTimeInMillis = currentTimeMillis()
        currentQuestion = questionIterator.next()
        val nextQuestion = currentQuestion!!
        val isLastQuestion = !questionIterator.hasNext()
        return NextQuestionEvent(nextQuestion.number, nextQuestion, isLastQuestion)
    }

    fun endGame(): Ranking {
        gameOver = true
        return scoreboard.ranking()
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun rank() = scoreboard.ranking()

    fun size() = quiz.questions.size
}

class Scoreboard {
    private val board = mutableMapOf<String, Long>()

    fun win(contestantId: String, score: Long) {
        board[contestantId] = board[contestantId] ?: (0 + score)
    }

    fun ranking(): Ranking {
        val sortedBoard = board.entries.sortedByDescending { it.value }
        val ranks = sortedBoard.mapIndexed { index, entry -> Rank(index + 1, entry.key, entry.value) }
        return Ranking(ranks)
    }
}

class Ranking(val ranks: List<Rank>) {
    fun rank(rankNumber: Int): Rank {
        if (rankNumber > ranks.size) {
            throw IllegalArgumentException("Cannot find the rank (by number $rankNumber).")
        }
        return ranks[rankNumber]
    }

    fun takeRangeRankings(range: Int): Map<Long, List<Rank>> {
        return ranks.filter { it.score > 0 }
            .groupBy { it.score }
            .toList()
            .sortedByDescending { it.first }
            .take(range)
            .toMap()
    }
}

data class Rank(val rankNumber: Int, val contestantId: String, var score: Long)


data class Quiz(val topic: String, val questions: List<Question>) {}

data class Question(
    val number: Int, val description: String, val options: List<String>,
    val type: QuestionType, val answer: AnswerSpec,
    val explanation: String? = null
) {
    fun isCorrectAnswer(answer: Answer): Boolean {
        return when (type) {
            QuestionType.SINGLE -> {
                val singleAnswerSpec = this.answer as? SingleAnswerSpec
                singleAnswerSpec?.optionNumber == (answer as SingleChoiceAnswer).optionNumber
            }

            QuestionType.MULTIPLE -> {
                val multipleAnswerSpec = this.answer as? MultipleAnswerSpec
                multipleAnswerSpec?.optionNumbers?.toSet() == (answer as MultipleChoicesAnswer).optionNumbers.toSet()
            }
        }
    }

    enum class QuestionType {
        SINGLE,
        MULTIPLE
    }
}

abstract class AnswerSpec()
data class SingleAnswerSpec(val optionNumber: Int) : AnswerSpec() {

}

data class MultipleAnswerSpec(val optionNumbers: List<Int>) : AnswerSpec() {
}

abstract class Answer(open val timestamp: Date) {

}

data class SingleChoiceAnswer(val optionNumber: Int, override val timestamp: Date = Date()) : Answer(timestamp) {

}

data class MultipleChoicesAnswer(val optionNumbers: List<Int>, override val timestamp: Date = Date()) :
    Answer(timestamp) {
}

enum class AnswerResult {
    WRONG, CORRECT
}
