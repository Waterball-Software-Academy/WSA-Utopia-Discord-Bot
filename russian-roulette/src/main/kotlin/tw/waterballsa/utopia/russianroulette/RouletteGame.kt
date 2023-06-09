package tw.waterballsa.utopia.russianroulette

class RouletteGame() {
    private val roulette = listOf(false, false, false, false, false, true).shuffled()
    private var currentTurn = 5
    var isGameOver = false
        private set

    fun pullTrigger() {
        if (roulette[currentTurn--]) {
            gameOver()
        }
    }

    private fun gameOver() {
        isGameOver = true
    }
}
