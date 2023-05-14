package tw.waterballsa.utopia.utopiagamificationquest.domain

abstract class Action {

    abstract class Criteria {

        var isCompleted: Boolean = false
            private set

        fun complete() {
            isCompleted = true
        }

        abstract fun isSuffice(action: Action): Boolean
    }

    abstract fun match(criteria: Criteria): Boolean

    fun updateProgress(criteria: Criteria) {
        if (criteria.isSuffice(this)) {
            criteria.complete()
        }
    }
}
