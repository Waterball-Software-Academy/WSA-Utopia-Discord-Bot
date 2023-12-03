package tw.waterballsa.utopia.utopiagamification.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.minigames.Player
import tw.waterballsa.utopia.minigames.PlayerFinder

@Component
class PlayerFinderAdapter(
    private val playerRepository: PlayerRepository
) : PlayerFinder {

    override fun findById(id: String): Player? {
        val player = playerRepository.findPlayerById(id) ?: return null
        return Player(player.id, player.bounty)
    }
}
