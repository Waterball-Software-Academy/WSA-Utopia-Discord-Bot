package tw.waterballsa.utopia.utopiaquiz

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiaquiz.domain.QuestionSet

class QuestionSetTest {
    private lateinit var questionSet: QuestionSet
    @BeforeEach
    fun init(){
        questionSet = QuestionSet()
    }
    @Test
    fun testGetRandomQuestions() {
        val questions = questionSet.getRandomQuestions(3)
        assertThat(questions.size).isEqualTo(3)
        assertThat(questions.size).isEqualTo(questions.distinct().size)
    }

    @Test
    fun testGetQuestionsByIds() {
        val questionIds = listOf(1, 2, 4)
        val questions = questionSet.getQuestionsByIds(questionIds)

        assertThat(questions[0].id).isEqualTo(1)
        assertThat(questions[0].description).isEqualTo("以下關於水球軟體學院的願景何者錯誤？")

        assertThat(questions[1].id).isEqualTo(2)
        assertThat(questions[1].description).isEqualTo("請問以下何者不是學院「三大 BUFF」？")

        assertThat(questions[2].id).isEqualTo(4)
        assertThat(questions[2].description).isEqualTo("以下何者言論遵守「紳士文化」？")
    }
}
