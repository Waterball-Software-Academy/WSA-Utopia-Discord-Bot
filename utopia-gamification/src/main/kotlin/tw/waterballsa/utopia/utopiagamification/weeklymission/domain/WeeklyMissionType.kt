package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

enum class WeeklyMissionType(
        private val id: Int
) {
    MESSAGE(0), VOICE(1);

    companion object {
        private val typeMap = mutableMapOf<Int, WeeklyMissionType>()
    }

    init {
        values().forEach {
            typeMap.put(it.id, it)
        }
    }

    fun isType(id : Int) : WeeklyMissionType =
            typeMap.get(id) ?: throw RuntimeException()
}
