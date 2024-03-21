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
        return playerRepository.findPlayerById(id)
            ?.toMiniGamePlayer()
    }

    private fun Player.toMiniGamePlayer(): MiniGamePlayer =
        MiniGamePlayer(id, bounty, lastSignInTime, continuousSignInDays)
}
