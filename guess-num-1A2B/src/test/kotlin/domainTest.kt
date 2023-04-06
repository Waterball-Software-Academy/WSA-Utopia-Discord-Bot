import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tw.waterballsa.utopia.guessnum1a2b.domain.GuessNum1A2B


class GuessNum1A2BTest {
    private val secretNumber = "1234"
    private val game = GuessNum1A2B(secretNumber)

    @Test
    fun `test when guess has only As`() {
        assertGuessResult("1567", "1A0B")
        assertGuessResult("1267", "2A0B")
        assertGuessResult("1236", "3A0B")
        assertGuessResult("1234", "4A0B")
    }

    @Test
    fun `test when guess has only Bs`() {
        assertGuessResult("5671", "0A1B")
        assertGuessResult("5621", "0A2B")
        assertGuessResult("5321", "0A3B")
    }

    @Test
    fun `test when guess has both As and Bs`() {
        assertGuessResult("1423", "1A3B")
        assertGuessResult("1243", "2A2B")
    }

    @Test
    fun `test when guess has neither As nor Bs`() {
        assertGuessResult("7890", "0A0B")
        assertGuessResult("123", "0A0B")
        assertGuessResult("12345", "0A0B")
    }

    @Test
    fun `test when guess is the same as the secret number`() {
        assertGuessResult(secretNumber, "4A0B")
    }

//    @Test
//    fun `test with invalid guess input`() {
//        assertThrows<IllegalArgumentException> { game.guess("12a4") }
//        assertThrows<IllegalArgumentException> { game.guess("12345") }
//        assertThrows<IllegalArgumentException> { game.guess("12") }
//        assertThrows<IllegalArgumentException> { game.guess("") }
//    }

    private fun assertGuessResult(guess: String, expected: String) {
        val result = try {
            game.guess(guess)
        } catch (ex: IllegalArgumentException) {
            throw AssertionError("Input validation failed for guess $guess", ex)
        }
        assertEquals(expected, result)
    }
}
