package tw.waterballsa.utopia.utopiagamification.quest.domain

abstract class Action(
    val playerId: String
) {

    abstract fun match(criteria: Criteria): Boolean

    fun execute(criteria: Criteria): Boolean = criteria.meet(this)

    abstract class Criteria {

        abstract fun meet(action: Action): Boolean
    }
}





