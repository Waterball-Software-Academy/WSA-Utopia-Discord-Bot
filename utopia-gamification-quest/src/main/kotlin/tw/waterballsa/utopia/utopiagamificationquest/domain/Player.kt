package tw.waterballsa.utopia.utopiagamificationquest.domain

import tw.waterballsa.utopia.utopiagamificationquest.LevelSheet.Companion.calculateLevel
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import kotlin.ULong.Companion.MIN_VALUE

class Player(
    val id: String,
    var name: String,
    var exp: ULong = MIN_VALUE,
    var level: UInt = 1u,
    val jdaRoles: MutableList<String> = mutableListOf(),
    val joinDate: OffsetDateTime = now(),
    var latestActivateDate: OffsetDateTime = now(),
    var levelUpgradeDate: OffsetDateTime = now()
) {
    init {
        calculateLevel()
    }

    fun gainExp(rewardExp: ULong) {
        exp += rewardExp
        calculateLevel()
        activate()
    }

    private fun calculateLevel() {
        val newLevel = calculateLevel(exp)
        if (newLevel > level) {
            level = newLevel
            levelUpgradeDate = now()
        }
    }

    private fun activate() {
        latestActivateDate = now()
    }
}
