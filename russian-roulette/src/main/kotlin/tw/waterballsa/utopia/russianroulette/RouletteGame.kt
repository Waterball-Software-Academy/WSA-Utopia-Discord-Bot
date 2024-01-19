package tw.waterballsa.utopia.russianroulette

import kotlin.random.Random.Default.nextInt

class RouletteGame {
    private var currentTurn = 6
    private var survivalRound = 0
    private val bountyRatios = mapOf(0 to -1, 1 to 1, 2 to 1, 3 to 2, 4 to 3, 5 to 4, 6 to 8)

    fun pullTrigger() {
        currentTurn--
    }

    private fun shouldEndGameEarly(): Boolean {
        return nextInt(0, currentTurn) == 0
    }

    private fun shouldEndGameLate(): Boolean {
        if (nextInt(0, 1) != 0) {
            survivalCount()
        }
        return true
    }

    fun isGameOver(): Boolean {
        return if (survivalRound < 5) {
            shouldEndGameEarly()
        } else {
            shouldEndGameLate()
        }
    }

    fun survivalCount() {
        survivalRound++
    }

    fun calculateBounty(playerBet: Int): Int {
        return bountyRatios[survivalRound]!!.times(playerBet)
    }
}
