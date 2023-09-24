package tw.waterballsa.utopia.gamification.quest.domain

abstract class Action(
    val player: Player
) {

    abstract fun match(criteria: Criteria): Boolean

    fun execute(criteria: Criteria): Boolean = criteria.meet(this)

    abstract class Criteria {

        abstract fun meet(action: Action): Boolean

        open val link: String = ""
    }
}





