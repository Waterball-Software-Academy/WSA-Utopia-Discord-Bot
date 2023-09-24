package tw.waterballsa.utopia.gamification.dailyrank.domain

class Settlement(
    val playerId: String,
    val dailyTotalExp: Int,
    val rankRaise: Int,
    val rank: Int,
    val events: DailyEvent
) {
}
