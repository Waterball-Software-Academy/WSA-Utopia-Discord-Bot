package tw.waterballsa.utopia.utopiagamification.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException

@Component
class PlayerFinderAdapter(
    private val playerRepository: PlayerRepository
) : PlayerFinder {

    override fun findById(id: String): MiniGamePlayer? {
        val player = findPlayer(id)
        return player.toMiniGamePlayer()
    }

    private fun findPlayer(playerId: String): Player =
        playerRepository.findPlayerById(playerId) ?: throw NotFoundException.notFound(Player::class).id(playerId)
            .build()

    private fun Player.toMiniGamePlayer(): MiniGamePlayer = MiniGamePlayer(id,  bounty)

}
