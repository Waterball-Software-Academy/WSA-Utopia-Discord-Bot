package tw.waterballsa.utopia.utopiagamificationquest.repository

import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

@Repository
class PlayerDataBase {
    
    private val players = hashMapOf<String, PlayerData>()

    fun findPlayerById(id: String): Player? {
        return players[id]?.toDomain()
    }

    fun savePlayer(player: Player) {
        players[player.id] = player.toData()

    }

    fun putIfAbsent(player: Player) {
        players.putIfAbsent(player.id, player.toData())
    }

    private fun Player.toData(): PlayerData {
        return PlayerData(id, name, exp, level)
    }
}

class PlayerData(
        private val id: String,
        private val name: String,
        private val exp: ULong,
        private val level: UInt
) {
    fun toDomain(): Player {
        return Player(id, name, exp, level)
    }
}
