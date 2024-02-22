package tw.waterballsa.utopia.guessnum1a2b.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GuessNum1A2BTest {
    private lateinit var game: GuessNum1A2B
    private var answer = "1234"

    @BeforeEach
    fun setup() {
        val gameId = GuessNum1A2B.Id("1", "1")
        game = GuessNum1A2B(gameId = gameId, answer = answer)
    }

    @Test
    @DisplayName(
        """
            number is invalid
                Given:
                    - answer = 1234
                When:
                    - number = 1/23
                    - player play in 1A2B game
                Then:
                    - return "The number (1/23) is invalid."
        """
    )
    fun `number is invalid`() {
        // When
        val number = "1/23"
        val guessNumber = game.guess(number)

        // Then
        assertThat(guessNumber).isEqualTo(listOf(AnsweredEvent("The number (1/23) is invalid.")))
    }

    @Test
    @DisplayName(
        """
            number is 0A2B
                Given:
                    - answer = 1234
                When:
                    - number = 5326
                    - player play in 1A2B game
                Then:
                    - return "0A2B"
        """
    )
    fun `number is 0A2B`() {
        // When
        val number = "5326"
        val guessNumber = game.guess(number)

        // Given
        assertThat(guessNumber).isEqualTo(listOf(AnsweredEvent("0A2B")))
    }

    @Test
    @DisplayName(
        """
            number is 4A
                Given:
                    - answer = 1234
                When:
                    - number = 1234
                    - player play in 1A2B game
                Then:
                    - return "4A"
        """
    )
    fun `number is 4A`() {
        val gameId = GuessNum1A2B.Id("1", "1")
        // When
        val number = "1234"
        val guessNumber = game.guess(number)

        // Given
        assertThat(guessNumber).isEqualTo(listOf(AnsweredEvent("4A0B")) + GameOverEvent(gameId))
    }
}
