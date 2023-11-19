package tw.waterballsa.utopia.utopiagamification.activity.domain

import mu.KotlinLogging
import tw.waterballsa.utopia.utopiagamification.activity.extensions.DateTimeRange
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.JoinActivityAction

private val log = KotlinLogging.logger {}

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

    fun leave(playerId: String): JoinActivityAction? {
        val audience = audiences[playerId] ?: return null
        val stayDuration = audience.leave()

        log.info(""" [leave activity] { "userId" = "$playerId", "activityName" = $eventName, "stayDuration" = "$stayDuration"} """)

        return JoinActivityAction(
            playerId,
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
