package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

enum class WeeklyMissionType(
        private val id: Int
) {
    MESSAGE(0), VOICE(1);

    companion object {
        private val typeMap = mutableMapOf<Int, WeeklyMissionType>()

        fun getWeeklyMissionType(id : Int): WeeklyMissionType {
            return typeMap[id] ?: throw  RuntimeException()
        }
    }

    init {
        values().forEach {
            typeMap[it.id] = it
        }
    }

}
