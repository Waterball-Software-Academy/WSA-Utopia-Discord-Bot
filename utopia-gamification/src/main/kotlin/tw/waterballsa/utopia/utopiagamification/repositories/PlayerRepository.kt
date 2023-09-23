package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

interface PlayerRepository {

    fun findPlayerById(id: String): Player?
    fun savePlayer(player: Player): Player
}
