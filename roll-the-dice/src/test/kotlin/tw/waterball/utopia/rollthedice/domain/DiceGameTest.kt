package tw.waterball.utopia.rollthedice.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.rollthedice.domain.DiceGame

class DiceGameTest {
    private lateinit var game: DiceGame

    @BeforeEach
    fun setup() {
        game = DiceGame()
    }

    @Test
    @DisplayName(
        """
            roll the true size and range of the dice
                Given:
                    - empty dice list
                When:
                    - roll the dice
                Then:
                    - dice size is 2
                    - dice range is 1 to 6
        """
    )
    fun `roll the true size and range of the dice`() {
        // Given
        var playerDice = mutableListOf<Int>()
        var computerDice = mutableListOf<Int>()

        // When
        game.rollTheDice()
        playerDice = game.getPlayerDice()
        computerDice = game.getComputerDice()

        // Then
        assertThat(playerDice).hasSize(2)
        assertThat(playerDice).allMatch{it in 1..6}
        assertThat(computerDice).hasSize(2)
        assertThat(computerDice).allMatch{it in 1..6}
    }


    @Test
    @DisplayName(
        """
            player win the game
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [3, 4]
                    - player vs computer in dice game
                Then:
                    - player win the game
        """
    )
    fun `player win the dice game`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(3, 4)
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("win")
    }

    @Test
    @DisplayName(
        """
            player lose the game
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [1, 2]
                    - player vs computer in dice game
                Then:
                    - player lose the game
        """
    )
    fun `player lose the dice game`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(1, 2)
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("lose")
    }

    @Test
    @DisplayName(
        """
            player game is even
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [2, 3]
                    - player vs computer in dice game
                Then:
                    - dice game is draw
        """
    )
    fun `game is draw`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(2, 3)
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("draw")
    }

    @Test
    @DisplayName(
        """
            player get 1 time of the bounty
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [3, 4]
                    - player bounty = 100
                    - player vs computer in dice game
                Then:
                    - dice game result is win
                    - player bounty get 1 time
        """
    )
    fun `player get 1 times of bounty`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(3, 4)
        val playerBounty = 100
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("win")
        assertThat(game.calculateBounty(playerBounty)).isEqualTo(playerBounty * 1)
    }

    @Test
    @DisplayName(
        """
            player loss the bounty
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [1, 2]
                    - player bounty = 100
                    - player vs computer in dice game
                Then:
                    - dice game result is win
                    - player loss the bounty
        """
    )
    fun `player loss the bounty`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(1, 2)
        val playerBounty = 100
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("lose")
        assertThat(game.calculateBounty(playerBounty)).isEqualTo(playerBounty * -1)
    }

    @Test
    @DisplayName(
        """
            player loss the bounty
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [3, 3]
                    - player bounty = 100
                    - player vs computer in dice game
                Then:
                    - dice game result is win
                    - player loss the bounty
        """
    )
    fun `player win twice of the bounty`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(3, 3)
        val playerBounty = 100
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("win")
        assertThat(game.calculateBounty(playerBounty)).isEqualTo(playerBounty * 2)
    }

    @Test
    @DisplayName(
        """
            player loss the bounty
                Given:
                    - computer dice = [2, 3]
                When:
                    - player dice = [6, 6]
                    - player bounty = 100
                    - player vs computer in dice game
                Then:
                    - dice game result is win
                    - player loss the bounty
        """
    )
    fun `player three twice of the bounty`() {
        // Given
        val computerDice = mutableListOf(2, 3)

        // When
        val playerDice = mutableListOf(6, 6)
        val playerBounty = 100
        game = DiceGame(playerDice, computerDice)

        // Then
        assertThat(game.gameResult()).isEqualTo("win")
        assertThat(game.calculateBounty(playerBounty)).isEqualTo(playerBounty * 3)
    }
}
