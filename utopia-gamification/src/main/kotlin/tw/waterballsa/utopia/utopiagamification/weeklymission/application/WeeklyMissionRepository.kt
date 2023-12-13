package tw.waterballsa.utopia.utopiagamification.weeklymission.application

import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.WeeklyMission

interface WeeklyMissionRepository {
    fun save(weeklyMission: WeeklyMission): WeeklyMission
}
