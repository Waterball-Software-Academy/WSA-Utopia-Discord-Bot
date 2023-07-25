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
    }

    private fun calculateLevel() {
        while (exp >= currentLevelExpLimit) {
            exp -= currentLevelExpLimit
            levelUp()
            currentLevelExpLimit = getLevelExpLimit(level)
        }
    }

    private fun levelUp() {
        level += 1u
    }
}

private const val EXP_PER_MIN = 10u
private val COEFFICIENT = listOf(1u, 2u, 4u, 8u, 12u, 16u, 32u, 52u, 64u, 84u)
private val UPGRATE_TIME_TABLE = mutableListOf<UInt>()
private fun getCoefficient(level: UInt): UInt {
    if (level > 100u) {
        return COEFFICIENT.last()
    }
    return COEFFICIENT[(level.toInt() - 1) / 10]
}

private fun calculateUpgrateTime(level: UInt, table: MutableList<UInt>): UInt {
    table.getOrElse(level.toInt()) {
        val result = if (level == 0u) {
            0u
        } else {
            calculateUpgrateTime(level - 1u, table) + EXP_PER_MIN * getCoefficient(level)
        }
        table.add(level.toInt(), result)
    }
    return table[level.toInt()]
}

private fun getLevelExpLimit(level: UInt): UInt {
    return calculateUpgrateTime(level, UPGRATE_TIME_TABLE) * EXP_PER_MIN
}
