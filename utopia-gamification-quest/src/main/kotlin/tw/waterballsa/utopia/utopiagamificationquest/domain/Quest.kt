package tw.waterballsa.utopia.utopiagamificationquest.domain

class Quest(
        val title: String,
        val description: String,
        val reward: Reward,
        val criteria: Action.Criteria,
        var nextQuest: Quest? = null
)

class Reward(
    val respond: String,
    val exp: ULong
)
