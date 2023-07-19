package tw.waterballsa.utopia.jda.domains

class QuizPreparationStartEvent(
    val quizTakerId: String,
    private val quizCallback: QuizCallback,
) : UtopiaEvent {

    fun startQuiz() = quizCallback.startQuiz()
    
    fun reply(message: String) = quizCallback.reply(message)
}

interface QuizCallback {

    fun startQuiz()
    fun reply(message: String)
}


class QuizEndEvent(
    val quizTakerId: String,
    val quizName: String,
    val score: Int
) : UtopiaEvent
