package tw.waterballsa.utopia.gamification.dailyrank.domain

open class DailyEvent(
    val playerId: String
) {


    enum class EventState {
        POSTED, NOT_POSTED
    }
}
