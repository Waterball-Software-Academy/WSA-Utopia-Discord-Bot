package tw.waterballsa.utopia.russianroulette

/**
 * 從輪盤遊戲中存活並得到獎金。
 * 使用 6 發子彈 :gun: 存活次數越多，獲得的獎金就越多（如果你在 5 次射擊中存活下來，則獲得 4 倍）。
 * 點擊 Pull the trigger 按鈕以繼續使用相同的 :gun: 並增加你的獎金風險自負。
 * 獎勵區間：依照進行回合數，決定獎金一倍至四倍
 * 第 1 回合 = 4 倍
 * 第 2 回合 = 3 倍
 * 第 3 回合 = 2 倍
 * 第 4 回合 = 1 倍
 */

//TODO:
// 1. 遊戲結束時依照上述規則計算遊戲獎勵，需扣除賭金後結算

class RouletteGame() {
    private val roulette = listOf(false, false, false, false, false, true).shuffled()
    private var currentTurn = 5

    fun pullTrigger() {
        currentTurn--
    }

    fun isGameOver(): Boolean = roulette[currentTurn]
}
