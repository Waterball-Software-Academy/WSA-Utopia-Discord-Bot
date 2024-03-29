package tw.waterballsa.utopia.knowledgeking.domain

import java.lang.Exception
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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

        try {
            return listOf(
                ContestStartedEvent(quiz.questions.size),
                NextQuestionEvent(1, this.currentQuestion!!, false)
            )
        } catch (err: Exception) {
            // TODO: often throw NullPointerException, inspect it
            return listOf()
        }
    }

    fun answer(contestantId: String?, answer: Answer): AnsweredEvent {
        val answerEvent = when {
            // end
            gameOver -> throw IllegalStateException("The game is over, cannot answer the question.")
            // correct
            currentQuestion?.isCorrectAnswer(answer) ?: false -> {
                scoreboard.getPoint(contestantId!!, calculateScore(currentQuestionStartTimeInMillis))
                AnsweredEvent(answer, contestantId, AnswerResult.CORRECT)
            }
            // incorrect
            else -> {
                scoreboard.answerWrong(contestantId!!, calculateScore(currentQuestionStartTimeInMillis))
                AnsweredEvent(answer, contestantId, AnswerResult.WRONG)
            }
        }
        return answerEvent
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

    fun isGameHalfway(): Boolean =
        (currentQuestion?.number ?: 0) == kotlin.math.ceil(getQuestionsSize().toDouble() / 2).toInt()

    fun rank(): Ranking = scoreboard.ranking()

    fun getQuestionsSize(): Int = quiz.questions.size

    fun getElapsedTimeInSeconds(questionStartTimeInSeconds: Long): Long {
        return currentTimeMillis().milliseconds.inWholeSeconds - questionStartTimeInSeconds.seconds.inWholeSeconds
    }

    /**
     * response sec     score
     * 0..1	            501
     * 1..2	            385
     * 2..3	            318
     * 3..4	            269
     * 4..5	            232
     * 5..6	            202
     * 6..7	            176
     * 7..8	            154
     * 8..9	            134
     * 9..10           	116
     * 10..11          	101
     * 11..12          	86
     * 12..13          	73
     * 13..14          	60
     * 14..15          	49
     *
     * formula: round((3 + ln(1/secondsElapsed)) * 167)
     */
    fun calculateScore(questionStartTimeInMillis: Long): Long {
        val secondsElapsed = ceil(minOf(maxOf(getElapsedTimeInSeconds(questionStartTimeInMillis.milliseconds.inWholeSeconds), 1), 15).toDouble())
        return round((3 + ln(1.0 / secondsElapsed)) * 167).toLong()
    }
}

class Scoreboard {
    private val board = mutableMapOf<String, Long>()

    fun getPoint(contestantId: String, score: Long) {
        board[contestantId] = (board[contestantId] ?: 0) + score
    }

    // TODO: 要計算到 negative 還是一切 reset zero，沒有的話後續可以考慮跟 getPoint 合併
    fun answerWrong(contestantId: String, score: Long) {
        board[contestantId] = (board[contestantId] ?: 0) - score
    }

    fun ranking(): Ranking {
        val sortedBoard = board.entries.sortedByDescending { it.value }
        val ranks = sortedBoard.mapIndexed { index, entry -> Rank(index + 1, entry.key, entry.value) }
        return Ranking(ranks)
    }
}

class Ranking(val ranks: List<Rank>, private val showRankingRange: Int = 3) {

    private var rankingGroup = mutableListOf<RankingGroup>()

    init {
        initRankingGroup()
    }

    fun rank(rankNumber: Int): Rank {
        if (rankNumber > ranks.size) {
            throw IllegalArgumentException("Cannot find the rank (by number $rankNumber).")
        }
        return ranks[rankNumber]
    }

    fun getRankingGroups() = rankingGroup

    private fun initRankingGroup() {
        rankingGroup.addAll(makeRankingGroup(showRankingRange))
    }

    /**
     * return Map<score: Long, List<Rank>>
     */
    private fun makeRankingGroup(range: Int): List<RankingGroup> {
        return ranks.filter { it.score > 0 }
            .groupBy { it.score }
            .toList()
            .sortedByDescending { it.first }
            .take(range)
            .mapIndexed { index, it -> RankingGroup(index + 1, it.first, it.second) }
    }

    data class RankingGroup(val rankingNum: Int, val score: Long, val ranks: List<Rank>) {
        fun asMentionsString(separator: String = " "): String {
            return ranks.joinToString(separator) { "<@${it.contestantId}>" }
        }
    }
}

data class Rank(val rankNumber: Int, val contestantId: String, var score: Long)


data class Quiz(val topic: String, val questions: List<Question>) {}

data class Question(
    val number: Int, val description: String, val options: List<String>,
    val type: QuestionType, val answer: AnswerSpec,
    val explanation: String? = null
) {
    var sentTimeInSeconds: Long? = null

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

    fun generateSentTime() { sentTimeInSeconds = currentTimeMillis().milliseconds.inWholeSeconds }

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
