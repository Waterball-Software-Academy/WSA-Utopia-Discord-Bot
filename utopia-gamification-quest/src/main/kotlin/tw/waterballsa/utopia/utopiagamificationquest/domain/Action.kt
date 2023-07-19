package tw.waterballsa.utopia.utopiagamificationquest.domain

abstract class Action(
        val player: Player
) {

    abstract fun match(criteria: Criteria): Boolean

    fun execute(criteria: Criteria) {
        if (criteria.meet(this)) {
            criteria.complete()
        }
    }
}

abstract class Criteria(
    private val goalCount: Int,
    private var completedTimes: Int = 0
) {

    var isCompleted: Boolean = false
        private set

    fun complete() {
        isCompleted = true
    }

    fun meet(action: Action): Boolean {
        return meetAction(action) && ++completedTimes == goalCount
    }


    protected abstract fun meetAction(action: Action): Boolean
}

