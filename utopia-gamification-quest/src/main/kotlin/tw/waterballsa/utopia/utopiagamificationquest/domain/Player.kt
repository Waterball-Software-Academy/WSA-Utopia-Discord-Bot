package tw.waterballsa.utopia.utopiagamificationquest.domain

import java.lang.IllegalArgumentException

class Player(val id: String, var name: String, var exp: ULong = 0u, level: UInt = 1u) {

    var level: UInt = 1u
         private set(value) {
             if (value >= 1u) {
                 field = value
             } else {
                 throw IllegalArgumentException("your level must more than 1")
             }
         }

    init {
        this.level = level
    }

    fun gainExp(rewardExp: ULong) {
        exp += rewardExp
    }
}
