package tw.waterballsa.utopia.russianroulette

import kotlin.random.Random.Default.nextInt

class RouletteGame() {
    private var currentTurn = 6
    private var survivalRound = 0
    private val betRule = mapOf<Int, Int>(0 to 0, 1 to 1, 2 to 1, 3 to 2, 4 to 3, 5 to 4, 6 to 8)

    fun pullTrigger() {
        currentTurn--
    }

    fun isGameOver(): Boolean {
        if (survivalRound < 5) {
            return nextInt(0, currentTurn) == 0
        } else if (nextInt(0, 1) != 0) {
            survivalCount()
        }
        return true
    }

    fun survivalCount() {
        survivalRound++
    }

    fun handleBounty(playerBet: Int): Int {
        return betRule[survivalRound]!!.times(playerBet)
    }
}
