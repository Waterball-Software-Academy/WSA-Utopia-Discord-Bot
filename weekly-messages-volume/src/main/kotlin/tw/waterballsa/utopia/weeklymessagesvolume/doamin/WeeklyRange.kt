package tw.waterballsa.utopia.weeklymessagesvolume.doamin

import java.time.LocalDate
import java.time.LocalDate.now
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * 前七天(含今天)的訊息量
 */
class WeeklyRange private constructor(
    private val startDateTime: OffsetDateTime,
    private val endDateTime: OffsetDateTime
) {
    constructor() : this(
        now().minusDays(7).toTaiwanDateTime(),
        now().plusDays(1).toTaiwanDateTime()
    )

    fun contains(dateTime: OffsetDateTime): Boolean =
        startDateTime.isBefore(dateTime) && dateTime.isBefore(endDateTime)
}

private fun LocalDate.toTaiwanDateTime(): OffsetDateTime =
    atStartOfDay().atZone(ZoneId.of("Asia/Taipei")).toOffsetDateTime()
