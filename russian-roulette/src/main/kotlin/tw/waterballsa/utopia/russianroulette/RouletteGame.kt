package tw.waterballsa.utopia.russianroulette

class RouletteGame() {
    private val roulette = listOf(false, false, false, false, false, true).shuffled()
    private var currentTurn = 5

    fun pullTrigger() {
        currentTurn--
    }

    fun isGameOver(): Boolean = roulette[currentTurn]
}
