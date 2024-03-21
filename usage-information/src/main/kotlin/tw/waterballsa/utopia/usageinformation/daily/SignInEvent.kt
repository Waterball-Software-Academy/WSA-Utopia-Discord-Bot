package tw.waterballsa.utopia.usageinformation.daily

import tw.waterballsa.utopia.jda.domains.UtopiaEvent
import java.time.OffsetDateTime


data class SignInEvent(
    val playerId: String,
    val bounty: Int,
    val lastSignInTime: OffsetDateTime,
    val continuousSignInDays: Int
) : UtopiaEvent
