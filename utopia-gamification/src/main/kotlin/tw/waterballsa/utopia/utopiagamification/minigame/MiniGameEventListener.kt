package tw.waterballsa.utopia.utopiagamification.minigame

import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.minigames.GameSettledEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException

class MiniGameEventListener(
    private val playerRepository: PlayerRepository
) : UtopiaListener() {

    //TODO:
    // 1. 用 UtopiaListener 的 onUtopiaEvent 接收結算事件
    private fun onMiniGamePlayerRewarded(event: GameSettledEvent) {
        with(event) {
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
