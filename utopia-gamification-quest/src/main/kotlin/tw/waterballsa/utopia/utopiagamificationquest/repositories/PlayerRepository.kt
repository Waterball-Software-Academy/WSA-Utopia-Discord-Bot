package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

interface PlayerRepository {
    fun findPlayerById(id: String): Player?
    fun savePlayer(player: Player): Player
}

@Component
class ImMemoryPlayerRepository : PlayerRepository {

    private val players = hashMapOf<String, Player>()

    override fun findPlayerById(id: String): Player? = players[id]

    override fun savePlayer(player: Player): Player = players.computeIfAbsent(player.id) { player }

    private fun Player.toData(): PlayerData = PlayerData(id, name, exp, level)
}

class PlayerData(
        private val id: String,
        private val name: String,
        private val exp: ULong,
        private val level: UInt
) {
    fun toDomain(): Player = Player(id, name, exp, level)
}
