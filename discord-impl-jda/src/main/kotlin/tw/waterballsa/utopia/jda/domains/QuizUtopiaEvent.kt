package tw.waterballsa.utopia.jda.domains

abstract class QuizPreparationStartEvent(
    val quizTakerId: String,
) : UtopiaEvent {

    abstract fun startQuiz()

    abstract fun reply(message: String)
}

class QuizEndEvent(
    val quizTakerId: String,
    val quizName: String,
    val correctCount: Int
) : UtopiaEvent
