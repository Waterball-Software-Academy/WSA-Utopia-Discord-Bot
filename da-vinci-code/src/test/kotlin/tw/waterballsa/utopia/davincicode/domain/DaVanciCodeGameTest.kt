package tw.waterballsa.utopia.davincicode.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private


class DaVanciCodeGameTest {
    private lateinit var game: DaVanciCodeGame

    @BeforeEach
    fun setup() {
        game = DaVanciCodeGame(50)
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
        println(game.guessNumber(25))
        println(game.isGameOver())
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
        println(game.guessNumber(50))
        println(game.isGameOver())
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
        println(game.guessNumber(25))
        println(game.isGameOver())
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
        println(game.calculateBounty(100))
    }
}
