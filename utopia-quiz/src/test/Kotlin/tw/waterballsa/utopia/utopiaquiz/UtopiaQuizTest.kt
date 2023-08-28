package tw.waterballsa.utopia.utopiaquiz


import org.assertj.core.api.Assertions.*
import org.junit.Test
import tw.waterballsa.utopia.utopiaquiz.domain.*
import java.lang.RuntimeException
import java.time.Duration
import java.time.LocalDateTime.*

internal class UtopiaQuizTest {

    private val quizTimeFromOneMinutesAgoWithTimeLimitFive = QuizTimeRange(now().minusMinutes(1), Duration.ofMinutes(5))
    private val quizTimeFromTenMinutesAgoWithTimeLimitTwo = QuizTimeRange(now().minusMinutes(10), Duration.ofMinutes(2))

    @Test
    fun `Given a quiz with 4 question and pass criteria 2 question, when answer 2 question correctly, then quiz passed`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromOneMinutesAgoWithTimeLimitFive)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 2))
        quiz.answerQuestion(Answer(3, 1))
        quiz.answerQuestion(Answer(4, 1))

        assertThat(quiz.pass()).isTrue()
    }

    @Test
    fun `Given a quiz with 4 question and pass criteria 2 question, when answer 1 question correctly, then quiz failed`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromOneMinutesAgoWithTimeLimitFive)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 1))
        quiz.answerQuestion(Answer(3, 1))
        quiz.answerQuestion(Answer(4, 1))

        assertThat(quiz.pass()).isFalse()
    }

    @Test
    fun `Given a quiz start from 10 minutes ago with time limit 2, when take the quiz, then the quiz is expired`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromTenMinutesAgoWithTimeLimitTwo)

        assertThat(quiz.isExpired()).isTrue()
    }

    @Test
    fun `Given a quiz start from 1 minutes ago with time limit 5, when take the quiz, then the quiz is not expired`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromOneMinutesAgoWithTimeLimitFive)

        assertThat(quiz.isExpired()).isFalse()
    }

    @Test
    fun `Given a quiz with 4 question, when answered the second question, then quiz is not all answered`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromOneMinutesAgoWithTimeLimitFive)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 1))

        assertThat(quiz.isAllAnswered()).isFalse()
    }

    @Test
    fun `Given a quiz in second question stage, when answered the first question, then it's not allowed to answer`() {
        val quiz = givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeFromOneMinutesAgoWithTimeLimitFive)

        quiz.answerQuestion(Answer(1, 1))

        assertThatThrownBy {
            quiz.answerQuestion(Answer(1, 1))
        }.isInstanceOf(RuntimeException::class.java)
    }

    private fun givenAQuizWithFourQuestionAndPassTwoQuestion(quizTimeRange: QuizTimeRange): Quiz = Quiz(
        QuizId("quiz taker1", "xxx Quiz"),
        QuizDefinition(2, 4),
        listOf(
            Question(1, "", listOf("option1", "option2", "option3", "option4"), 1),
            Question(2, "", listOf("option1", "option2", "option3", "option4"), 2),
            Question(3, "", listOf("option1", "option2", "option3", "option4"), 3),
            Question(4, "", listOf("option1", "option2", "option3", "option4"), 4)
        ),
        quizTimeRange
    )
}
