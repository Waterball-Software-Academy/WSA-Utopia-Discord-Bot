package tw.waterballsa.utopia.utopiagamification.minigame

import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.minigames.PlayerBountySettledEvent
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

class MiniGameEventListener(
    private val playerRepository: PlayerRepository
) : UtopiaListener() {
    private fun onPlayerRewarded(event: PlayerBountySettledEvent) {
        with(event) {
            val player = playerRepository.findPlayerById(id) ?: return
            player.gainBounty(bounty)
            playerRepository.savePlayer(player)
        }
    }
}
