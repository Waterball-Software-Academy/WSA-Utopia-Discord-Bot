package tw.waterballsa.utopia.gamification.dailyrank.domain

class QuestRewardClaimedEvent(
    val completedQuestTitle: String,
    val gainExp: Int, playerId: String
) : DailyEvent(playerId = playerId) {
}

