package tw.waterballsa.utopia.utopiagamificationquest.domain

import org.slf4j.event.Level
import java.lang.IllegalArgumentException

class Player(val id: String, var name: String, var exp: Long, level: Int) {
    var level: Int = 0
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

    fun levelUp() {
        level += 1
    }
}

