package tw.waterballsa.utopia.minigames

data class PlayerBountySettledEvent(
    val id: String,
    val name: String,
    val bounty: UInt
)
