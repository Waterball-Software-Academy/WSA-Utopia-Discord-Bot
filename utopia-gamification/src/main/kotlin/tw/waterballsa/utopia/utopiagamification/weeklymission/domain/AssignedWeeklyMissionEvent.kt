package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

data class AssignedWeeklyMissionEvent (
        private val gentlemanId : String,
        private val weeklyMissions: List<WeeklyMission>
){
}
