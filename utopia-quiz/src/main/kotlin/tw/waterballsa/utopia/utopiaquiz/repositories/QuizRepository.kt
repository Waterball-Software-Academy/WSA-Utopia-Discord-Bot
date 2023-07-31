package tw.waterballsa.utopia.utopiaquiz.repositories

import tw.waterballsa.utopia.utopiaquiz.domain.Quiz
import tw.waterballsa.utopia.utopiaquiz.domain.QuizId

interface QuizRepository {

    fun findQuizById(id: QuizId): Quiz?
    fun saveQuiz(quiz: Quiz): Quiz
}


