package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import java.time.Instant

class JoinVoiceChannelAction(
        val voiceChannelId: String,
        val accumulatedHeadCount: Int,
        val startTime: Instant
) {
    fun progress(joinVoiceChannelMission: JoinVoiceChannelMission) {
        joinVoiceChannelMission.progress(this)
    }


}
