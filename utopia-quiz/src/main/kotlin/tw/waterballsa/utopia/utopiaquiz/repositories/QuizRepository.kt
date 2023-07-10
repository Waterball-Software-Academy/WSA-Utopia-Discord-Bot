package tw.waterballsa.utopia.utopiaquiz.repositories

import tw.waterballsa.utopia.utopiaquiz.domain.Quiz
import tw.waterballsa.utopia.utopiaquiz.domain.QuizId

interface QuizRepository {
    fun findQuizById(id: String): Quiz

    fun saveQuiz(quiz: Quiz): Quiz
}


class InMemoryQuizRepository(val quiz: Quiz) {

    fun findQuizById(id: QuizId): Quiz {
        TODO()
    }

    fun saveQuiz(quiz: Quiz): Quiz {
        TODO()
    }

}

