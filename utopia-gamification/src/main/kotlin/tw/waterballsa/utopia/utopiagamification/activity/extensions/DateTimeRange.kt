package tw.waterballsa.utopia.utopiagamification.activity.extensions

import java.time.Duration
import java.time.LocalDateTime

class DateTimeRange(
    private var startTime: LocalDateTime = LocalDateTime.now(),
    private var endTime: LocalDateTime = startTime
) {

    fun getDuration(): Duration = Duration.between(startTime, endTime)

    fun end() {
        endTime = LocalDateTime.now()
    }

    fun getStartTime(): String = startTime.toString()

    fun getEndTime(): String = endTime.toString()
}
