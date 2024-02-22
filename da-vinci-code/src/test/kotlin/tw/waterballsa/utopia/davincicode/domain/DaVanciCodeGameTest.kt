package tw.waterballsa.utopia.davincicode.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class DaVanciCodeGameTest {
    private lateinit var game: DaVanciCodeGame
    private val fixedFinalNumber = 50

    @BeforeEach
    fun setup() {
        game = DaVanciCodeGame(fixedFinalNumber)
    }

    @Test
    @DisplayName(
        """
        測試遊戲結束判斷是否正確 -> 玩家猜錯
        given：
            - 預設遊戲還有 5 回合
            - 終極密碼為 50
        when：
            - 玩家 A 猜測數字為 25
        then：
            - 告訴玩家最終數字的範圍判斷
            - 剩下四回合
            - 遊戲尚未結束，玩家可繼續猜測
        """
    )
    fun `test game over or not when lose`() {
        assertThat(game.guessNumber(25)).isEqualTo("最終數字大於 25，你還有 4 次機會。")
        assertThat(game.isGameOver()).isFalse()
    }

    @Test
    @DisplayName(
        """
        測試遊戲結束判斷是否正確 -> 玩家猜對
        given：
            - 預設遊戲還有 5 回合
            - 終極密碼為 50
        when：
            - 玩家 A 猜測數字為 50
        then：
            - 告訴玩家最終數字為 50
            - 遊戲結束，玩家獲勝
        """
    )
    fun `test game over or not when win`() {
        assertThat(game.guessNumber(50)).isEqualTo("恭喜答對了，最終數字為 ${fixedFinalNumber}，獲得賞金 $")
        assertThat(game.isGameOver()).isTrue()
    }

    @Test
    @DisplayName(
        """
        當回合數用完時的判斷 -> 玩家未猜對
        given：
            - 遊戲已經過了四回合，剩下最後一回
            - 終極密碼為 50
        when：
            - 玩家 A 猜測數字為 25
        then：
            - 遊戲結束，告知玩家正確的最終數字為 50
        """
    )
    fun `when running out of game turn`() {
        for (i in 1..4){
            game.guessNumber(2)
        }
        assertThat(game.guessNumber(25)).isEqualTo("你沒有找到最終數字，最終數字為 ${fixedFinalNumber}，賞金 ")
        assertThat(game.isGameOver()).isTrue()
    }

    @Test
    @DisplayName(
        """
        測試賞金計算是否正確 -> 玩家第一回合就猜中
        given：
            - 遊戲開始，預設還有五回合
            - 玩家 A 賭注為 $100
            - 終極密碼為 50
        when：
            - 玩家 A 猜測數字為 50
        then：
            - 玩家 A 猜測正確
            - 獲得獎金倍數 10 倍
            - 拿到獎金 $1000
        """
    )
    fun `test bounty calculation`() {
        game.guessNumber(50)
        assertThat(game.calculateBounty(100)).isEqualTo(1000)
    }
}
