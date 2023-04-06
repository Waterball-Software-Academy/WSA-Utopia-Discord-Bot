package tw.waterballsa.utopia.guessnum1a2b.domain


open class Event() {}

data class GameStartedEvent(val gameId: GuessNum1A2B.Id) : Event() {
}

data class AnsweredEvent(val answer: String) : Event() {
}

data class GameOverEvent(val gameId: GuessNum1A2B.Id) : Event() {
}
