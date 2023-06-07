package tw.waterballsa.utopia.gamification.commands

import org.slf4j.event.Level
import java.lang.IllegalArgumentException

class Player(val id: Snowflake, var name: String, var exp: Long, level: Int) {
    var level: Int
        set(value) {
            if (value >= 1) {
                field = value
            } else {
                throw IllegalArgumentException("your level must more than 1")
            }
        }

    init {
        this.level = level
    }

    fun gainExp(exp: Long) {
        this.exp += exp
    }

    // level up
    fun levelUp() {
        level += 1
    }
}
