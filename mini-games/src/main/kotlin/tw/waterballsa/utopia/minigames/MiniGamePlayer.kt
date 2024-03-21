package tw.waterballsa.utopia.minigames

import java.time.OffsetDateTime

data class MiniGamePlayer(
    val id: String,
    var bounty: Int,
    var lastSignInTime: OffsetDateTime? = null,
    var continuousSignInDays: Int? = 0
)
