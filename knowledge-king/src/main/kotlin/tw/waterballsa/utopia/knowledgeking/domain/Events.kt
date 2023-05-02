package tw.waterballsa.utopia.knowledgeking.domain

open class Event() {}

data class ContestStartedEvent(val numberOfQuestions: Int) : Event() {
}

data class NextQuestionEvent(val questionNumber: Int, val question: Question, val isLastQuestion: Boolean = false) : Event() {

}

data class AnsweredEvent(val answer: Answer, val contestantId: String, val answerResult: AnswerResult) : Event() {
}
