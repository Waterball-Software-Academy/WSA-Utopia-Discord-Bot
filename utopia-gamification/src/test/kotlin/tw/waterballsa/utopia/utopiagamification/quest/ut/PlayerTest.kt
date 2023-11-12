package tw.waterballsa.utopia.utopiagamification.quest.ut

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

class PlayerTest {

    @Test
    fun `given playerA level 3 and exp 650, when player gain 6000 exp, then level is 11 and exp is 6650`() {
        val player = Player("A", "A", 650u, 3u)
        val rewardExp = 6000uL
        player.gainExp(rewardExp)

        Assertions.assertThat(player.exp).isEqualTo(6650uL)
        Assertions.assertThat(player.level).isEqualTo(11u)
    }
}
