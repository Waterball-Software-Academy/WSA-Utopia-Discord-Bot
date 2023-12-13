package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward
import java.time.Duration
import java.time.Instant

class JoinVoiceChannelMission(
        val gentlemanId : String,
        val voiceChannelId: String,
        val leastHeadCount: Int,
        val timeRange: Long
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

    fun progress(joinVoiceChannelAction: JoinVoiceChannelAction): CompletedMissionEvent? {
        if (!isValidVoiceChannelId(joinVoiceChannelAction.voiceChannelId)) {
            return null
        }
        val duration = Duration.between(joinVoiceChannelAction.startTime, Instant.now()).toMinutes()
        if (isReachedHeadCount(joinVoiceChannelAction.accumulatedHeadCount) && isReachedTimeRange(duration)) {
            return CompletedMissionEvent(reward)
        }
        return null
    }


    fun isValidVoiceChannelId(voiceChannelId: String): Boolean = this.voiceChannelId == voiceChannelId

    fun isReachedHeadCount(accumulatedHeadCount: Int): Boolean = this.leastHeadCount <= accumulatedHeadCount

    fun isReachedTimeRange(voiceChannelDuration: Long): Boolean = this.timeRange <= voiceChannelDuration


}
