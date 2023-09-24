package tw.waterballsa.utopia.gamification.dailyrank.domain

class Rank {

    val playerIdToRankPlayer: Map<String, Player> = mutableMapOf()

    fun refresh(dailyEvents: List<DailyEvent>): List<Settlement> {
        return emptyList()
    }

    private fun sort() {

    }

    data class Player(
        val playerId: String,
        val exp: Int,
        val level: Int
    )
}
