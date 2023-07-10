package tw.waterballsa.utopia.utopiaquiz.domain

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiaquiz.repositories.QuizRepository

class QuestionSet() {
    private val questions: List<Question> = getQuestions()

    private fun getQuestions(): List<Question> {
//        wait for QuizRepository implementation
        return listOf(Question("問題一", listOf("A","B","C","D"), 0),
            Question("問題二", listOf("A","B","C","D"), 3),
            Question("問題三", listOf("A","B","C","D"), 1),
            Question("問題四", listOf("E","F","G","H"), 2),
            Question("問題五", listOf("A","B","C","D"), 1)
            )
    }

    fun getRandomQuestions(number: Int): List<Question> {
        return questions.shuffled().take(number).toList()
    }
}
