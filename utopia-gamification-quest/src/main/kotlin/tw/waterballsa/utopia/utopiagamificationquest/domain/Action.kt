package tw.waterballsa.utopia.utopiagamificationquest.domain

import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.*

abstract class Action(
    val player: Player
) {

    abstract fun match(criteria: Criteria): Boolean

    fun execute(criteria: Criteria) {
        if (criteria.meet(this)) {
            criteria.complete()
        }
    }




    abstract class Criteria {

        var isCompleted: Boolean = false
            private set

        fun complete() {
            isCompleted = true
        }

        abstract fun meet(action: Action): Boolean
}



