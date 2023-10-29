package tw.waterballsa.utopia.utopiagamification.quest.extensions

import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.LevelRange.Companion.LEVEL_ONE
import java.lang.String.format


class LevelSheet private constructor() {

    companion object {
        private const val EXP_PER_MINUTES = 10u
        private const val MAX_LEVEL = 100
        private val COEFFICIENTS = arrayOf(1u, 2u, 4u, 8u, 12u, 16u, 32u, 52u, 64u, 84u)
        private val LEVEL_TO_LEVEL_RANGE = generateSequence(LEVEL_ONE) { it.next() }.take(MAX_LEVEL).associateBy { it.level }

        // exp to level
        fun ULong.toLevel() = (LEVEL_TO_LEVEL_RANGE.values.find { it.isMatchedLevel(this) } ?: LEVEL_ONE).level.toUInt()

        // level to level range
        fun UInt.toLevelRange(): LevelRange = LEVEL_TO_LEVEL_RANGE[toInt()] ?: throw IllegalArgumentException("The given level ($this) not found.")
    }

    class LevelRange private constructor(val level: Int = 1, val previous: LevelRange? = null) {

        // 升級時間
        val upgradeTime: ULong

        // 累積經驗值
        val accExp: ULong

        // 當前經驗值上限
        val expLimit: ULong

        companion object {
            val LEVEL_ONE = LevelRange(level = 1)
        }

        init {
            // 經驗值係數
            val coefficient = COEFFICIENTS[level.coerceAtLeast(1).div(10).coerceAtMost(COEFFICIENTS.size.minus(1))]
            upgradeTime = (previous?.upgradeTime ?: 0u).plus(EXP_PER_MINUTES.times(coefficient))
            accExp = (previous?.accExp ?: 0u).plus(EXP_PER_MINUTES.times(upgradeTime))
            expLimit = accExp.minus(previous?.accExp ?: 0u)
        }

        fun next() = LevelRange(level.plus(1), this)

        fun isMatchedLevel(exp: ULong) = accExp > exp

        override fun toString(): String = format("| level: %3d | upgrade time: %5d | exp limit: %6d | acc exp: %7d | %n${"-".repeat(75)}",
            level, upgradeTime.toLong(), expLimit.toLong(), accExp.toLong())
    }
}
