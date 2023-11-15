package tw.waterballsa.utopia.gamification.leaderboard.domain

data class LeaderBoardItem(
    val playerId: String,
    val name: String,
    val exp: ULong,
    val level: UInt,
    val bounty: UInt,
    var rank: Int = 0,
)
