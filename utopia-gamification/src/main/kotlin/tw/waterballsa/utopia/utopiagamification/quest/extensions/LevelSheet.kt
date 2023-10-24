package tw.waterballsa.utopia.utopiagamification.quest.extensions

import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.Range.Companion.LEVEL_ONE
import kotlin.ULong.Companion.MIN_VALUE

class LevelSheet private constructor() {

    companion object {
        const val EXP_PER_MINUTES = 10u
        private const val MAX_LEVEL = 100
        private val COEFFICIENTS = arrayOf(1u, 2u, 4u, 8u, 12u, 16u, 32u, 52u, 64u, 84u)
        private val LEVEL_TO_RANGE = generateSequence(LEVEL_ONE) { it.next() }.take(MAX_LEVEL).associateBy { it.level }

        fun calculateLevel(exp: ULong) = (LEVEL_TO_RANGE.values.find { it.isExpGreaterThan(exp) } ?: LEVEL_ONE).level.toUInt()

        fun getLevelRange(level: Int): Range = when {
            level <= 0 -> LEVEL_ONE
            level > MAX_LEVEL -> LEVEL_TO_RANGE.values.last()
            else -> LEVEL_TO_RANGE[level] ?: throw IllegalArgumentException("The level ($level) is incorrect.")
        }
    }

    class Range private constructor(val level: Int = 1, previousLevelRange: Range? = null) {

        // 升級時間
        val upgradeTime: ULong

        // 累積經驗值
        val accExp: ULong

        // 當前經驗值上限
        val expLimit: ULong

        companion object {
            val LEVEL_ONE = Range()
        }

        init {
            // 經驗值係數
            val coefficient = COEFFICIENTS[level.coerceAtLeast(1).div(10).coerceAtMost(COEFFICIENTS.size.minus(1))]
            upgradeTime = (previousLevelRange?.upgradeTime ?: MIN_VALUE).plus(EXP_PER_MINUTES.times(coefficient))
            accExp = (previousLevelRange?.accExp ?: MIN_VALUE).plus(EXP_PER_MINUTES.times(upgradeTime))
            expLimit = accExp.minus(previousLevelRange?.accExp ?: MIN_VALUE)
        }

        fun next() = Range(level.plus(1), this)

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
