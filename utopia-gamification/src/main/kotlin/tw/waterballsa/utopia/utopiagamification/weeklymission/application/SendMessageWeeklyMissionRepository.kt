package tw.waterballsa.utopia.utopiagamification.weeklymission.application

import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.SendMessageMission
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.WeeklyMission

interface SendMessageWeeklyMissionRepository {
    fun save(sendMessageMission: SendMessageMission): SendMessageMission
}
