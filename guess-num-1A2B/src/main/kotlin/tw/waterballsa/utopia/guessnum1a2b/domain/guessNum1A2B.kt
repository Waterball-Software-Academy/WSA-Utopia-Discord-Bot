package tw.waterballsa.utopia.guessnum1a2b.domain

const val correctAnswer = "4A0B"

class GuessNum1A2B(private val secretNumber: String) {
    private var isGuessedRight = false
    fun guess(number: String): String {
        if (number.length != 4) {
            return "0A0B"
        }
        var a = 0
        var b = 0
        for (i in 0 until 4) {
            if (number[i] == secretNumber[i]) {
                a++
            } else if (secretNumber.contains(number[i])) {
                b++
            }
        }

        if (a == 4) {
            isGuessedRight = true
        }

        return "${a}A${b}B"
    }

    fun isGuessedRight(): Boolean {
        return isGuessedRight
    }

}

fun generateSecretNumber(): String {
    return (0..9).shuffled().take(4).joinToString("")
}
