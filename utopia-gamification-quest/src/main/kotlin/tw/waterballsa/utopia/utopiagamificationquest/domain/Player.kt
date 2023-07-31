package tw.waterballsa.utopia.utopiagamificationquest.domain

import tw.waterballsa.utopia.utopiagamificationquest.extensions.LevelSheet.Companion.calculateLevel
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import kotlin.ULong.Companion.MIN_VALUE

class Player(
    val id: String,
    var name: String,
    var exp: ULong = MIN_VALUE,
    var level: UInt = 1u,
    val joinDate: OffsetDateTime = now(),
    var latestActivateDate: OffsetDateTime = now(),
    var levelUpgradeDate: OffsetDateTime = now(),
    val jdaRoles: MutableList<String> = mutableListOf(),
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
        var explimit = getLevelExpLimit(level)
        while (exp >= explimit) {
            exp -= explimit
            level++
            explimit = getLevelExpLimit(level)
            levelUpgradeDate = now()
        }
    }

    private fun calculateLevel1() {
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

private const val EXP_PER_MIN = 10u
private val COEFFICIENT = listOf(1u, 2u, 4u, 8u, 12u, 16u, 32u, 52u, 64u, 84u)
private val UPGRADE_TIME_TABLE = mutableListOf<UInt>()
private fun getCoefficient(level: UInt): UInt {
    if (level > 100u) {
        return COEFFICIENT.last()
    }
    return COEFFICIENT[(level.toInt() - 1) / 10]
}

private fun calculateUpgradeTime(level: UInt, table: MutableList<UInt>): UInt {
    table.getOrElse(level.toInt()) {
        val result = if (level == 0u) {
            0u
        } else {
            calculateUpgradeTime(level - 1u, table) + EXP_PER_MIN * getCoefficient(level)
        }
        table.add(level.toInt(), result)
    }
    return table[level.toInt()]
}

private fun getLevelExpLimit(level: UInt): UInt {
    return calculateUpgradeTime(level, UPGRADE_TIME_TABLE) * EXP_PER_MIN
}
