package tw.waterballsa.utopia.utopiaquiz.domain

import java.time.LocalDateTime

class Quiz (
    val id: QuizId,
    private val quizDefinition: QuizDefinition,
    private val questionSet: QuestionSet,
//    the best data type of startTime/expiredTime?
    private val startTime: LocalDateTime,
    private val expiredTime: LocalDateTime
) {
    private var correctCount: Int = 0
    private var currentQuestionNumber: Int = -1
    private val quizQuestionSet: List<Question> = questionSet.getRandomQuestions(quizDefinition.totalQuestions)

    fun getNextQuestion(): Question {
        currentQuestionNumber += 1
        return quizQuestionSet[currentQuestionNumber]
    }

    fun answerQuestion(questionNumber: Int, choice: Int): Boolean {
        val answer = Answer(questionNumber, choice)
//        if answer.questionNumber attribute needed?
        return quizQuestionSet[questionNumber].verify(answer)
    }

    fun pass(): Boolean {
        return correctCount >= quizDefinition.requiredCorrectCount
    }

    fun isOver(): Boolean {
        val isLastQuestion = currentQuestionNumber == quizDefinition.totalQuestions
        val isTimeOver = LocalDateTime.now() > expiredTime
        return isLastQuestion || isTimeOver
    }

}
