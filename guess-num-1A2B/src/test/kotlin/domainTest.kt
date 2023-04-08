import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.guessNum1A2B.domain.GuessNum1A2B

class DomainTest {
    private val game = GuessNum1A2B("1234")

    @Test
    fun `only A condition`() {
        assertEquals(game.guess("1567"), "1A0B")
        assertEquals(game.guess("1267"), "2A0B")
        assertEquals(game.guess("1236"), "3A0B")
        assertEquals(game.guess("1234"), "4A0B")

    }

    @Test
    fun `only B condition`() {
        assertEquals(game.guess("5671"), "0A1B")
        assertEquals(game.guess("5621"), "0A2B")
        assertEquals(game.guess("5321"), "0A3B")

    }

    @Test
    fun `There is A and There is B`() {
        assertEquals(game.guess("1423"), "1A3B")
        assertEquals(game.guess("1243"), "2A2B")
    }

    @Test
    fun `neither A nor B`() {
        assertEquals(game.guess("7890"), "0A0B")
        assertEquals(game.guess("123"), "0A0B")
        assertEquals(game.guess("12345"), "0A0B")
    }

    @Test
    fun `guess the number`() {
        assertEquals(game.guess("4321"), "0A4B")
    }

}
