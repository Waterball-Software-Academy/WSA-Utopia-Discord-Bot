package tw.waterballsa.utopia.gamesinfo.domain

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.MessageEmbed

class GamesInfo {
    fun diceGame(commandId: String): MessageEmbed {
        return Embed {
            title = "dice"
            description = "</dice:$commandId>"
        }
    }

    fun guessNumberGame(commandId: String): MessageEmbed {
        return Embed {
            title = "guess number"
            description = "</dice:$commandId>"
            field { name = "guess number" }
        }
    }

    fun rockPaperScissorsGame(commandId: String): MessageEmbed {
        return Embed {
            title = "rock paper scissors"
            description = "</dice:$commandId>"
        }
    }

    fun rouletteGame(commandId: String): MessageEmbed {
        return Embed {
            title = "roulette"
            description = "</dice:$commandId>"
        }
    }

    fun guess1a2bGame(commandId: String): MessageEmbed {
        return Embed {
            title = "1a2b"
            description = "</dice:$commandId>"
        }
    }
}
