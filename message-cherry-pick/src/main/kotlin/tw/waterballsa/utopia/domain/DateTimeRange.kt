package tw.waterballsa.utopia.domain

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

class DateTimeRange(
    private val startDate: OffsetDateTime,
    private val endDate: OffsetDateTime
) {

    fun isStartTimeAfterEndTime(): Boolean = startDate.isAfter(endDate)

    fun contains(dateTime: OffsetDateTime): Boolean = startDate.isBefore(dateTime) && dateTime.isBefore(endDate)

    override fun toString(): String =
        "The Range is ${startDate.format(ISO_LOCAL_DATE)} between ${endDate.format(ISO_LOCAL_DATE)}"
}

fun OffsetDateTime.rangeTo(dateTime: OffsetDateTime): DateTimeRange = DateTimeRange(this, dateTime)
