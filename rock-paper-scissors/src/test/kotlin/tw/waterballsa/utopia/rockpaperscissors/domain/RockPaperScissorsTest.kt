package tw.waterballsa.utopia.rockpaperscissors.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RockPaperScissorsTest {
    private lateinit var game: RockPaperScissors

    @BeforeEach
    fun setup() {
        // 剪刀->石頭->布，後者贏前者
        game = RockPaperScissors()
    }

    @Test
    @DisplayName(
        """
            player in rock paper scissors game is winner
                Given:
                    - enemy = rock
                When:
                    - player = paper
                    - player vs enemy in rock paper scissors game
                Then:
                    - player win the game
        """
    )
    fun `my punch win`() {
        // Given
        val enemyPunch = Punch.ROCK

        // When
        val myPunch = Punch.PAPER
        val gamePunch = game.punch(myPunch = myPunch, enemyPunch = enemyPunch)

        // Then
        assertThat(gamePunch).isEqualTo(PunchResult.WIN)
    }

    @Test
    @DisplayName(
        """
            player in rock paper scissors game is loser
                Given:
                    - enemy = rock
                When:
                    - player = scissors
                    - player vs enemy in rock paper scissors game
                Then:
                    - player lose the game
        """
    )
    fun `my punch lose`() {
        // Given
        val enemyPunch = Punch.ROCK

        // When
        val myPunch = Punch.SCISSORS
        val gamePunch = game.punch(myPunch = myPunch, enemyPunch = enemyPunch)

        // Given
        assertThat(gamePunch).isEqualTo(PunchResult.LOSE)
    }

    @Test
    @DisplayName(
        """
            player in rock paper scissors game is even
                Given:
                    - enemy = rock
                When:
                    - player = rock
                    - player vs enemy in rock paper scissors game
                Then:
                    - game is even
        """
    )
    fun `my punch in game is even`() {
        // Given
        val enemyPunch = Punch.ROCK

        // When
        val myPunch = Punch.ROCK
        val gamePunch = game.punch(myPunch = myPunch, enemyPunch = enemyPunch)

        // Given
        assertThat(gamePunch).isEqualTo(PunchResult.EVEN)
    }
}
