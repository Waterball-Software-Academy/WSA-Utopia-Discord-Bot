package tw.waterballsa.utopia.gamification.repositories

import tw.waterballsa.utopia.gamification.quest.domain.Player

interface PlayerRepository {

    fun findPlayerById(id: String): Player?
    fun savePlayer(player: Player): Player
}
