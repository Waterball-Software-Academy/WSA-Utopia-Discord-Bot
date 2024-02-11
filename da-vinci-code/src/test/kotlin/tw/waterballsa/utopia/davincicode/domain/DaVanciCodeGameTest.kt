package tw.waterballsa.utopia.davincicode.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class DaVanciCodeGameTest {
    private lateinit var game: DaVanciCodeGame

    @BeforeEach
    fun setup() {
        game = DaVanciCodeGame(50)
    }

    @Test
    fun temp() {
        game.guessNumber(25)
        println(game.isGameOver())
    }

}
