package tw.waterballsa.utopia.rollthedice.domain


class DiceGame(playerDice: MutableList<Int> ?= null, computerDice: MutableList<Int> ?= null) {
    private var playerDice = playerDice?: mutableListOf()
    private var computerDice = computerDice?: mutableListOf()

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
            "lose"
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
        } else if (result == "lose") {
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
