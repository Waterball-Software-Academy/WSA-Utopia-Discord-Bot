package tw.waterballsa.utopia.guessNum1A2B.domain

class GuessNum1A2B(private val secretNumber: String) {

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
        return "${a}A${b}B"
    }

}

fun generateSecretNumber(): String {
    return (0..9).shuffled().take(4).joinToString("")
}
