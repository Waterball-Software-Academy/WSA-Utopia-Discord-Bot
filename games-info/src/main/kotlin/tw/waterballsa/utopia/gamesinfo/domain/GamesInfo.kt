package tw.waterballsa.utopia.gamesinfo.domain

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.MessageEmbed

class GamesInfo {
    fun diceGame(commandId: String): MessageEmbed {
        return Embed {
            color = 457723
            field {
                name = "**骰子遊戲**"
                value = """
                    </dice:$commandId> `<bounty>`
                    骰兩顆骰子並嘗試戰勝你的對手。當你骰到 Double（兩個一樣）就會得到雙倍獎金，骰到兩個 6 就會得到三倍獎金。
                    獎勵區間取：取決於你的點數，獲取一倍到三倍獎金
                    """.trimIndent()
            }
        }
    }

    fun guessNumberGame(commandId: String): MessageEmbed {
        return Embed {
            color = 457723
            field {
                name = "**終極密碼（DaVanci Code）**"
                value = """
                    </guess:$commandId> `<bounty>`
                    從 1 至 100，在五回合內猜出終極密碼可獲得獎金。用得回合越少，獎金越多！（每少用一回合，獎金為10倍的機率增加）
                    獎勵區間取：1 至 10 倍賭注，獎金取決於使用的回合，如未猜中則扣除 1 倍賞金。
                    - 1 回 → 10 倍
                    - 2 回 → 7 倍
                    - 3 回 → 5 倍
                    - 4 回 → 2 倍
                    - 5 回 → 猜中 1 倍／沒猜中 -1 倍
                """.trimIndent()
            }
        }
    }

    fun rockPaperScissorsGame(commandId: String): MessageEmbed {
        return Embed {
            color = 457723
            field {
                name = "**猜拳（剪刀石頭布）**"
                value = """
                    </rps:$commandId> `<bounty>`
                    剪刀石頭布戰勝你的對手來獲得你的賭注
                    獎勵區間取：
                    - 獲勝：1 倍賭注
                    - 平手：拿回籌碼
                    - 敗北：拿走籌碼
                """.trimIndent()
            }
        }
    }

    fun rouletteGame(commandId: String): MessageEmbed {
        return Embed {
            color = 457723
            field {
                name = "**輪盤**"
                value = """
                    </roulette:$commandId> `<bounty>`
                    從輪盤遊戲中存活並得到獎金，使用 6 發子彈 :gun: 存活次數越多，獲得的獎金就越多，點擊 Pull the trigger 按鈕以繼續使用相同的 :gun: 並增加你的獎金風險自負。

                    獎勵區間：依照進行回合數，決定獎金一倍至四倍
                    * 存活 0 回合 = 0 倍
                    * 存活 1 回合 = 1 倍
                    * 存活 2 回合 = 1 倍
                    * 存活 3 回合 = 2 倍
                    * 存活 4 回合 = 3 倍
                    * 存活 5 回合 = 4 倍
                    * 存活 6 回合 = 8 倍
                """.trimIndent()
            }
        }
    }

    fun guess1a2bGame(commandId: String): MessageEmbed {
        return Embed {
            color = 457723
            field {
                name = "**1A2B**"
                value = """
                    </1a2b:$commandId> `<bounty>`
                    一～三回合：獎金 1.25 倍
                    四～六回合：獎金 1 倍
                    七回合以上：獎金 0.5 倍
                """.trimIndent()
            }
        }
    }
}
