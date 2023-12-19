package tw.waterballsa.utopia.minigames

import tw.waterballsa.utopia.jda.domains.UtopiaEvent

data class GameSettledEvent(
    val playerId: String,
    val bounty: UInt
) : UtopiaEvent
