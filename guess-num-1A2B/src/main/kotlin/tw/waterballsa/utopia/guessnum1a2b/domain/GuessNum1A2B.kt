package tw.waterballsa.utopia.guessnum1a2b.domain



class GuessNum1A2B(
    private val gameId: Id,
    val answer: String = generateAnswer()
) {
    data class Id(val playerId: String, val roomId: String)

    fun startGame(): List<Event> {
        return listOf(GameStartedEvent(gameId))
    }

    fun guess(number: String): List<Event> {
        if (!isValidNumber(number)) {
            return listOf(AnsweredEvent("The number (${number}) is invalid."))
        }

        var a = 0
        var b = 0
        for (i in 0 until 4) {
            if (number[i] == answer[i]) {
                a++
            } else if (answer.contains(number[i])) {
                b++
            }
        }

        val events = listOf(AnsweredEvent("${a}A${b}B"))
        if (a == 4) {
            return events + GameOverEvent(gameId)
        }
        return events
    }

    private fun isValidNumber(number: String): Boolean {
        return number matches Regex("^(?!.*(.).*\\1)\\d{4}\$")
    }
}

private fun generateAnswer(): String {
    return (0..9).shuffled().take(4).joinToString("")
}
