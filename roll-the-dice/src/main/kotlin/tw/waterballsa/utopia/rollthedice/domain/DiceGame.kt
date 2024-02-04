package tw.waterballsa.utopia.rollthedice.domain


class DiceGame {
    private var playerDice = mutableListOf<Int>()
    private var computerDice = mutableListOf<Int>()

    fun rollTheDice() {
        for (i in 0..1) {
            playerDice.add(i, (1..6).random())
            computerDice.add(i, (1..6).random())
        }
    }

    fun gameResult(): String {
        val computerDiceSum = computerDice.sum()
        val playerDiceSum = playerDice.sum()

        return if (playerDiceSum > computerDiceSum) {
            "win"
        } else if (playerDiceSum < computerDiceSum) {
            "loss"
        } else {
            "draw"
        }
    }

    fun calculateBounty(playerBet: Int): Int {
        val result = gameResult()
        return if (result == "win") {
            if (playerDice[0] == playerDice[1]) {
                if (playerDice[0] == 6 && playerDice[1] == 6) {
                    playerBet * 3
                } else {
                    playerBet * 2
                }
            } else {
                playerBet
            }
        } else if (result == "loss") {
            -playerBet
        } else {
            playerBet
        }
    }

    fun getPlayerDice(): MutableList<Int> {
        return playerDice
    }

    fun getComputerDice(): MutableList<Int> {
        return computerDice
    }
}
