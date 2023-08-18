package tw.waterballsa.utopia.utopiagamificationquest.domain

import mu.KotlinLogging
import tw.waterballsa.utopia.utopiagamificationquest.domain.Audience.State.*
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
    state: State,
    val dateTimeRange: DateTimeRange = DateTimeRange(),
    val audiences: MutableMap<String, Audience> = mutableMapOf()
) {

    var state = state
        private set

    fun join(player: Player) {
        if (state != State.ACTIVE) {
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
            stayDuration.toMinutes().toInt()
        )
    }

    fun cancel() {
        state = State.CANCELED
        dateTimeRange.end()
    }

    enum class State {
        SCHEDULED,
        ACTIVE,
        COMPLETED,
        CANCELED
    }
}



class Audience(
    val id: String,
    state: State = STAY,
    val joinTime: DateTimeRange = DateTimeRange()
) {

    var state = state
        private set

    fun leave(): Duration {
        joinTime.end()
        state = LEAVE
        return joinTime.getDuration()
    }

    enum class State {
        STAY,
        LEAVE
    }
}


