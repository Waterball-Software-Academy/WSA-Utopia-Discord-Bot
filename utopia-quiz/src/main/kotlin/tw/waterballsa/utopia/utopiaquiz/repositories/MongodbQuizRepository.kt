package tw.waterballsa.utopia.utopiaquiz.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiaquiz.domain.*

@Component
class MongodbQuizRepository(
    private val repository: MongoCollection<QuizDocument, QuizId>,
    private val questionSet: QuestionSet
) : QuizRepository {

    override fun findQuizById(id: QuizId): Quiz? = repository.findOne(id)?.toDomain()

    override fun saveQuiz(quiz: Quiz): Quiz = repository.save(quiz.toDocument()).toDomain()

    private fun QuizDocument.toDomain(): Quiz {
        val quizDefinition = QuizDefinition(requiredCorrectCount, totalQuestions)
        val quizQuestions = questionSet.getQuestionsByIds(questionIds)

        return Quiz(
            id,
            quizDefinition,
            quizQuestions,
            QuizTimeRange(startTime.toDate(), expiredTime.toDate()),
            correctCount,
            currentQuestionNumber,
            answerCount
        )
    }

    private fun Quiz.toDocument(): QuizDocument = QuizDocument(
        id,
        quizDefinition.requiredCorrectCount,
        quizDefinition.totalQuestions,
        quizTimeRange.startTime.toString(),
        quizTimeRange.expiredTime.toString(),
        correctCount,
        currentQuestionNumber,
        quizQuestions.map { it.id },
        answerCount
    )
}

@Document
data class QuizDocument(
    @Id val id: QuizId,
    val requiredCorrectCount: Int,
    val totalQuestions: Int,
    val startTime: String,
    val expiredTime: String,
    val correctCount: Int,
    val currentQuestionNumber: Int,
    val questionIds: List<Int>,
    val answerCount: Int
)
