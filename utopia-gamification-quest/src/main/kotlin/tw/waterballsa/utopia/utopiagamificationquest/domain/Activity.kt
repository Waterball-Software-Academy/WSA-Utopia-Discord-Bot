package tw.waterballsa.utopia.utopiagamificationquest.domain

import mu.KotlinLogging
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.JoinActivityAction
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now

private val log = KotlinLogging.logger {}

class DateTimeRange(
    private var startTime: LocalDateTime = now(),
    private var endTime: LocalDateTime = startTime
) {

    fun getDuration(): Duration = Duration.between(startTime, endTime)

    fun inTimeRange(): Boolean = startTime == endTime && now().isAfter(startTime.minusMinutes(20))

    fun end() {
        endTime = now()
    }

    fun getStartTime(): String = startTime.toString()

    fun getEndTime(): String = endTime.toString()
}

class Activity(
    val eventId: String,
    val hostId: String,
    val eventName: String,
    val channelId: String,
    val dateTimeRange: DateTimeRange = DateTimeRange(),
    val audiences: MutableMap<String, Audience> = mutableMapOf()
) {

    fun join(player: Player) {
        if (!dateTimeRange.inTimeRange()) {
            return
        }

        audiences[player.id] = player.toAudience()

        log.info(""" [join activity] { "userId" = "${player.id}", "activityName" = "$eventName"} """)
    }

    private fun Player.toAudience(): Audience = Audience(id)

    fun leave(player: Player): JoinActivityAction? {
        val audience = audiences[player.id] ?: return null
        val stayDuration = audience.leave()

        log.info(""" [leave activity] { "userId" = "${player.id}", "activityName" = $eventName, "stayDuration" = "$stayDuration"} """)

        return JoinActivityAction(
            player,
            eventName,
            audiences.size,
            //TODO 因為方便測試，時間單位為秒，上線前要改成分鐘
            stayDuration.seconds.toInt()
        )
    }

    fun end() = dateTimeRange.end()
}

class Audience(
    val id: String,
    val joinTime: DateTimeRange = DateTimeRange()
) {

    fun leave(): Duration {
        joinTime.end()
        return joinTime.getDuration()
    }
}
