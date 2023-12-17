package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

class JoinVoiceChannelAction(
        val player: Player,
        val voiceChannelId: String,
        val accumulatedHeadCount: Int
) {
    fun progress(joinVoiceChannelMission: JoinVoiceChannelMission) {
        joinVoiceChannelMission.progress(this)
    }


}
