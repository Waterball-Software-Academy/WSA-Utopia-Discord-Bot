package tw.waterballsa.utopia.davincicode.domain

import kotlin.random.Random.Default.nextInt

class DaVanciCodeGame {
    private val finalNumber = nextInt(1, 100)
    private var remainingAttempts = 5
    private var states = "loss"
    private var isGameOver = false
    private val bountyRatios = mapOf(0 to 1, 1 to 2, 2 to 5, 3 to 7, 4 to 10)

    fun guessNumber(playerGuessNumber: Int): String {
        remainingAttempts--

        return if (remainingAttempts == 0 || playerGuessNumber == finalNumber) {
            gameEnd(playerGuessNumber)
        } else if (playerGuessNumber > finalNumber) {
            return "最終數字小於 ${playerGuessNumber}，你還有 ${remainingAttempts} 次機會。"
        } else {
            return "最終數字大於 ${playerGuessNumber}，你還有 ${remainingAttempts} 次機會。"
        }
    }

    fun gameEnd(playerGuessNumber: Int): String {
        isGameOver = true

        if (playerGuessNumber != finalNumber) {
            return "你沒有找到最終數字，最終數字為 ${finalNumber}，賞金 "
        }
        states = "win"
        return "恭喜答對了，最終數字為 ${finalNumber}，獲得賞金 $"
    }

    fun isGameOver(): Boolean {
        return isGameOver
    }

    fun calculateBounty(playerBet: Int): Int {
        if (states == "win") {
            return bountyRatios[remainingAttempts]!!.times(playerBet)
        }
        return -playerBet
    }
}
