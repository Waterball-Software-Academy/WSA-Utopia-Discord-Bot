package tw.waterballsa.utopia.gamification.activity.domain

import tw.waterballsa.utopia.gamification.activity.extensions.DateTimeRange
import java.time.Duration

class Audience(
    val id: String,
    state: State = State.STAY,
    val joinTime: DateTimeRange = DateTimeRange()
) {

    var state = state
        private set

    fun leave(): Duration {
        joinTime.end()
        state = State.LEAVE
        return joinTime.getDuration()
    }

    enum class State {
        STAY,
        LEAVE
    }
}
