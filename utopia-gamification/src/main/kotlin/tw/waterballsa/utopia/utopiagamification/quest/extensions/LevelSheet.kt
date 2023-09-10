package tw.waterballsa.utopia.utopiagamification.quest.extensions

import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.LevelRange.Companion.LEVEL_ONE
import kotlin.ULong.Companion.MIN_VALUE

class LevelSheet private constructor() {

    companion object {
        const val EXP_PER_MINUTES = 10u
        private const val MAX_LEVEL = 100
        private val COEFFICIENTS = arrayOf(1u, 2u, 4u, 8u, 12u, 16u, 32u, 52u, 64u, 84u)
        private val LEVEL_RANGES = generateSequence(LEVEL_ONE) { it.next() }.take(MAX_LEVEL)

        fun calculateLevel(exp: ULong) = (LEVEL_RANGES.find { it.isExpGreaterThan(exp) } ?: LEVEL_ONE).level.toUInt()
    }

    private class LevelRange private constructor(val level: Int = 1, previousLevelRange: LevelRange? = null) {

        // 升級時間
        private val upgradeTime: ULong

        // 累積經驗值
        private val accExp: ULong

        // 當前經驗值上限
        private val expLimit: ULong

        companion object {
            val LEVEL_ONE = LevelRange()
        }

        init {
            // 經驗值係數
            val coefficient = COEFFICIENTS[level.coerceAtLeast(1).div(10).coerceAtMost(COEFFICIENTS.size.minus(1))]
            upgradeTime = (previousLevelRange?.upgradeTime ?: MIN_VALUE).plus(EXP_PER_MINUTES.times(coefficient))
            accExp = (previousLevelRange?.accExp ?: MIN_VALUE).plus(EXP_PER_MINUTES.times(upgradeTime))
            expLimit = accExp.minus(previousLevelRange?.accExp ?: MIN_VALUE)
        }

        fun next() = LevelRange(level.plus(1), this)

        fun isExpGreaterThan(exp: ULong) = accExp > exp

        override fun toString(): String {
            return String.format(
                "level: %3d, upgrade time: %5d, exp limit: %6d, acc exp: %7d",
                level,
                upgradeTime.toLong(),
                expLimit.toLong(),
                accExp.toLong()
            )
        }
    }
}
