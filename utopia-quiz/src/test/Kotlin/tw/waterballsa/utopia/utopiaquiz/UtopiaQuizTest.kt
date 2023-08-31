package tw.waterballsa.utopia.utopiaquiz


import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import tw.waterballsa.utopia.utopiaquiz.domain.*
import java.time.Duration
import java.time.LocalDateTime.now

internal class UtopiaQuizTest {

    private val ongoingQuizTime = QuizTimeRange(now().minusMinutes(1), Duration.ofMinutes(5))
    private val expiredQuizTime = QuizTimeRange(now().minusMinutes(10), Duration.ofMinutes(2))

    @Test
    fun `Given a quiz with 4 question and pass criteria 2 question, when answer 2 question correctly, then quiz passed`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), ongoingQuizTime)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 2))
        quiz.answerQuestion(Answer(3, 1))
        quiz.answerQuestion(Answer(4, 1))

        assertThat(quiz.pass()).isTrue()
    }

    @Test
    fun `Given a quiz with 4 question and pass criteria 2 question, when answer 1 question correctly, then quiz failed`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), ongoingQuizTime)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 1))
        quiz.answerQuestion(Answer(3, 1))
        quiz.answerQuestion(Answer(4, 1))

        assertThat(quiz.pass()).isFalse()
    }

    @Test
    fun `Given a quiz stopped from 8 minutes ago, then quiz is expired`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), expiredQuizTime)

        assertThat(quiz.isExpired()).isTrue()
    }

    @Test
    fun `Given an ongoing quiz, then quiz is not expired`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), ongoingQuizTime)

        assertThat(quiz.isExpired()).isFalse()
    }

    @Test
    fun `Given a quiz with 4 question, when answered the second question, then quiz is not all answered`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), ongoingQuizTime)

        quiz.answerQuestion(Answer(1, 1))
        quiz.answerQuestion(Answer(2, 1))

        assertThat(quiz.isAllAnswered()).isFalse()
    }

    @Test
    fun `Given a quiz in second question stage, when answered the first question, then it's not allowed to answer`() {
        val quiz = givenAQuizWithQuestions(QuizDefinition(2, 4), ongoingQuizTime)

        quiz.answerQuestion(Answer(1, 1))

        assertThatThrownBy {
            quiz.answerQuestion(Answer(1, 1))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun givenAQuizWithQuestions(quizDefinition: QuizDefinition, quizTimeRange: QuizTimeRange): Quiz = Quiz(
        QuizId("quiz taker1", "xxx Quiz"),
        quizDefinition,
        listOf(
            Question(1, "", listOf("option1", "option2", "option3", "option4"), 1),
            Question(2, "", listOf("option1", "option2", "option3", "option4"), 2),
            Question(3, "", listOf("option1", "option2", "option3", "option4"), 3),
            Question(4, "", listOf("option1", "option2", "option3", "option4"), 4)
        ),
        quizTimeRange
    )
}
