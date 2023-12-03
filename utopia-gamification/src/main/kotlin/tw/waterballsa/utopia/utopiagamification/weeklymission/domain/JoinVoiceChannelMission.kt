package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

class JoinVoiceChannelMission(
        val gentlemanId : String,
        val voiceChannelId: String,
        val leastHeadCount: Int,
        val timeRange: Int
): WeeklyMission() {

    init {
        var exp = 0uL
        exp += when (leastHeadCount) {
            in 1..10 -> 80uL
            in 11..40 -> 150uL
            in 41..80 -> 200uL
            in 81..150 -> 250uL
            else -> 250uL
        }
        exp += when (timeRange) {
            in 1..10 -> 40uL
            in 11..30 -> 60uL
            in 31..50 -> 80uL
            in 51..60 -> 100uL
            else -> 100uL
        }
        reward = Reward(
                exp, 0uL, 0f
        )
    }


    fun isValidVoiceChannelId(voiceChannelId: String): Boolean = this.voiceChannelId == voiceChannelId

    fun isReachedHeadCount(maximumHeadCount: Int): Boolean = this.leastHeadCount <= maximumHeadCount

    fun isReachedTimeRange(voiceChannelDuration: Int): Boolean = this.timeRange <= voiceChannelDuration

    fun isComplete(): Boolean {
        return false
    }


}
