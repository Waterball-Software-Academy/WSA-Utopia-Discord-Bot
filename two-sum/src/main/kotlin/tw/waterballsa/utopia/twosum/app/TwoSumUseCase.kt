package tw.waterballsa.utopia.twosum.app

import mu.KotlinLogging
import tw.waterballsa.utopia.twosum.domain.TwoSum

class TwoSumUseCase {
    private val logger = KotlinLogging.logger {}
    fun sum(a: Int, b: Int): String {
        logger.info { "sum $a + $b" }
        val twoSum: TwoSum = TwoSum(a, b)
        return twoSum.sum(username = "testing")
    }
}
