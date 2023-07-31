package tw.waterballsa.utopia.utopiaquiz.domain

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.*

class Quiz(
    val id: QuizId,
    val quizDefinition: QuizDefinition,
    val quizQuestions: List<Question>,
    val quizTimeRange: QuizTimeRange,
    correctCount: Int = 0,
    currentQuestionNumber: Int = 0,
    answerCount: Int = 0
) {

    constructor(
        id: QuizId,
        quizDefinition: QuizDefinition,
        questionSet: QuestionSet,
        quizTimeRange: QuizTimeRange
    ) : this(
        id,
        quizDefinition,
        questionSet.getRandomQuestions(quizDefinition.totalQuestions),
        quizTimeRange
    )

    var correctCount: Int = correctCount
        private set

    var answerCount: Int = answerCount
        private set

    var currentQuestionNumber: Int = currentQuestionNumber
        private set

    fun getNextQuestion(): Question = quizQuestions[currentQuestionNumber++]

    fun answerQuestion(questionNumber: Int, choice: Int): Boolean {
        val answer = Answer(questionNumber, choice)
        answerCount++
        if (quizQuestions[answer.questionNumber - 1].verify(answer.choice)) {
            correctCount++
            return true
        }
        return false
    }

    fun pass(): Boolean = correctCount >= quizDefinition.requiredCorrectCount

    fun isOver(): Boolean {
        val isLastQuestion = answerCount >= quizDefinition.totalQuestions
        val isTimeOver = quizTimeRange.contains(now()).not()
        return isLastQuestion || isTimeOver
    }
}

class QuizTimeRange(
    val startTime: LocalDateTime,
    val expiredTime: LocalDateTime
) {
    constructor(
        startTime: LocalDateTime,
        duration: Duration
    ) : this(startTime, startTime + duration)

    fun contains(time: LocalDateTime): Boolean = time.isAfter(startTime) && time.isBefore(expiredTime)
}

fun String.toDate(): LocalDateTime = parse(this)
