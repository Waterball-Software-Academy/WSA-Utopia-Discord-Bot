package tw.waterballsa.utopia.russianroulette

class RouletteGame() {
    private val roulette = listOf(false, false, false, false, false, true).shuffled() // random // shuffle -> 直接改變
    private var currentTurn = 5

    fun pullTrigger(): Boolean = roulette[currentTurn--]
}
