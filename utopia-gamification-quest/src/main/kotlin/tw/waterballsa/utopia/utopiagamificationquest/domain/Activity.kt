package tw.waterballsa.utopia.utopiagamificationquest.domain

import mu.KotlinLogging
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.JoinActivityAction
import tw.waterballsa.utopia.utopiagamificationquest.repositories.ActivityDocument
import tw.waterballsa.utopia.utopiagamificationquest.repositories.AudienceDocument
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

    fun setEndTimeAsCurrentTime() {
        endTime = now()
    }

    fun getStartTime(): String = startTime.toString()

    fun getEndTime(): String = endTime.toString()
}

class Activity(
    private val eventId: String,
    private val hostId: String,
    private val eventName: String,
    private val channelId: String,
    private val dateTimeRange: DateTimeRange = DateTimeRange(),
    private val audiences: MutableMap<String, Audience> = mutableMapOf()
) {

    fun join(audience: Audience) {
        if (!dateTimeRange.inTimeRange()) {
            return
        }

        audiences[audience.id] = audience

        log.info(""" [join activity] { "userId" = "${audience.id}", "activityName" = "$eventName"} """)
    }

    fun leave(audienceId: String): JoinActivityAction? {
        val audience = audiences[audienceId] ?: return null
        val stayDuration = audience.leave()

        log.info(""" [leave activity] { "userId" = "$audienceId", "activityName" = $eventName, "stayDuration" = "$stayDuration"} """)

        return JoinActivityAction(
            audience.toPlayer(),
            eventName,
            audiences.size,
            //TODO 因為方便測試，時間單位為秒，上線前要改成分鐘
            stayDuration.seconds.toInt()
        )
    }

    fun end() = dateTimeRange.setEndTimeAsCurrentTime()

    fun toDocument(): ActivityDocument = ActivityDocument(
        eventId,
        hostId,
        eventName,
        channelId,
        dateTimeRange.getStartTime(),
        dateTimeRange.getEndTime(),
        audiences.values.map { it.toDocument() }
    )
}

class Audience(
    val id: String,
    private val audienceName: String,
    private val joinTime: DateTimeRange = DateTimeRange()
) {
    fun leave(): Duration {
        joinTime.setEndTimeAsCurrentTime()
        return joinTime.getDuration()
    }

    fun toPlayer(): Player = Player(id, audienceName)

    fun toDocument(): AudienceDocument =
        AudienceDocument(id, audienceName, joinTime.getStartTime(), joinTime.getEndTime())
}
