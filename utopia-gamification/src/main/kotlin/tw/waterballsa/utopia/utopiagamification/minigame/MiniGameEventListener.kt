package tw.waterballsa.utopia.utopiagamification.minigame

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.UtopiaEvent
import tw.waterballsa.utopia.minigames.GameSettledEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException

@Component
class MiniGameEventListener(
    private val playerRepository: PlayerRepository
) : UtopiaListener() {
    override fun onUtopiaEvent(event: UtopiaEvent) {
        //TODO: 檢查資料庫 bounty 為什麼沒有變化
        if (event is GameSettledEvent) {
            val playerId = event.playerId
            val bounty = event.bounty
            val player = findPlayer(playerId)
            player.settleBounty(bounty)
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
