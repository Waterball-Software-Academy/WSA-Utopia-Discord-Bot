package tw.waterballsa.utopia.guessnum1a2b

import tw.waterballsa.utopia.guessnum1a2b.domain.GuessNum1A2B

class GameManager {
    private var games = mutableMapOf<GuessNum1A2B.Id, GuessNum1A2B>()

    fun register(gameId: GuessNum1A2B.Id): GuessNum1A2B {
        val game = GuessNum1A2B(gameId)
        games[gameId] = game
        logger.info { "[register] {  \"hostId\" : \"${gameId.playerId}\" }" }
        return game
    }

    fun unregister(gameId: GuessNum1A2B.Id) {
        games.remove(gameId)
        logger.info { "[unregister] {  \"hostId\" : \"${gameId.playerId}\" }" }
    }

    fun find(gameId: GuessNum1A2B.Id): GuessNum1A2B? {
        return games[gameId]
    }

    fun isAvailableGame(playerId: String): Boolean {
        return games.keys.any { it.playerId == playerId }
    }
}
