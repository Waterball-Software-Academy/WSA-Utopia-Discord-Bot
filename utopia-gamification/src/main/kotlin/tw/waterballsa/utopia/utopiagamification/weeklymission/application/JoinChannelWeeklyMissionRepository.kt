package tw.waterballsa.utopia.utopiagamification.weeklymission.application

import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.JoinVoiceChannelMission

interface JoinChannelWeeklyMissionRepository {
    fun save(joinVoiceChannelMission: JoinVoiceChannelMission): JoinVoiceChannelMission
}
