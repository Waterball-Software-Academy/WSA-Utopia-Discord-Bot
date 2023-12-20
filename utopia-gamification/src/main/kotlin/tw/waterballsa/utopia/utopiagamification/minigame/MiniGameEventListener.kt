package tw.waterballsa.utopia.utopiagamification.minigame

import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.UtopiaEvent
import tw.waterballsa.utopia.minigames.GameSettledEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException

class MiniGameEventListener(
    private val playerRepository: PlayerRepository
) : UtopiaListener() {
    override fun onUtopiaEvent(event: UtopiaEvent) {
        if (event is GameSettledEvent) {
            val playerId = event.playerId
            val bounty = event.bounty
            val player = findPlayer(playerId)
            player.gainBounty(bounty)
            playerRepository.savePlayer(player)
        }
    }

    private fun findPlayer(playerId: String): Player {
        return playerRepository.findPlayerById(playerId) ?: throw NotFoundException
            .notFound(Player::class)
            .id(playerId)
            .build()
    }
}
