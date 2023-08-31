package tw.waterballsa.utopia.utopiaquiz.domain

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse

class Quiz(
    val id: QuizId,
    val quizDefinition: QuizDefinition,
    val quizQuestions: List<Question>,
    val quizTimeRange: QuizTimeRange,
    correctCount: Int = 0,
    currentQuestionNumber: Int = 1,
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

    fun getCurrentQuestion(): Question = quizQuestions[currentQuestionNumber - 1]

    fun answerQuestion(answer: Answer): Boolean {
        val currentQuestion = getCurrentQuestion()

        if (currentQuestionNumber != answer.questionNumber) {
            throw IllegalArgumentException("無法回答這一題")
        }

        answerCount++
        currentQuestionNumber++

        if (currentQuestion.verify(answer.choice)) {
            correctCount++
            return true
        }

        return false
    }

    fun pass(): Boolean = correctCount >= quizDefinition.requiredCorrectCount
    fun isExpired() = quizTimeRange.contains(now()).not()
    fun isAllAnswered() = answerCount >= quizDefinition.totalQuestions
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
