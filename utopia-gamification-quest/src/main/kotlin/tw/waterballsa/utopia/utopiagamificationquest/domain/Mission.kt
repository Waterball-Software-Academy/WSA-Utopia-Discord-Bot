package tw.waterballsa.utopia.utopiagamificationquest.domain

class Mission(val player: Player, val quest: Quest, var missionId: Int = 0) {
    var isCompleted = false
        private set

    init {
        criteriaComplete()
    }

    fun match(action: Action): Boolean = action.match(quest.criteria)

    fun updateProgress(action: Action): Boolean {
        if (!action.match(quest.criteria)) {
            return false
        }
        action.updateProgress(quest.criteria)
        criteriaComplete()
        return true
    }

    private fun criteriaComplete() {
        isCompleted = quest.criteria.isCompleted
    }

    fun givePlayerExp() {
        player.gainExp(quest.reward.exp)
    }
    
    fun isNewMission(): Boolean {
        return missionId == 0
    }
}
