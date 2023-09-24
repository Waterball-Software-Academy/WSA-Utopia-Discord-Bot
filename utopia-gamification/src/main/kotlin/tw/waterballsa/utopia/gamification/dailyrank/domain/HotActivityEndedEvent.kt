package tw.waterballsa.utopia.gamification.dailyrank.domain

class HotActivityEndedEvent(
    val activityName: String,
    val audienceCount: Int,
    playerId: String
) : DailyEvent(playerId = playerId) {
}
