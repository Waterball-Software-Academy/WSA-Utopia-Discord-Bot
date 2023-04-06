import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.GuessNum1A2B.domain.GuessNum1A2B

class DomainTest {



    @Test
    fun `Test Guess Flow`(){
        val game = GuessNum1A2B("1234")

        assert(game.guess("5678") == "0A0B")
        assert(game.guess("9012") == "0A2B")
        assert(game.guess("0123") == "0A3B")
        assert(game.guess("1234") == "4A0B")

    }
}
