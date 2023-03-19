package tw.waterballsa.utopia.twosum.app

import tw.waterballsa.alpha.wsabot.domain.TwoSum

class TwoSumUseCase {
    fun sum(a: Int, b: Int): String {
        val twoSum: TwoSum = TwoSum(a, b)
        return twoSum.sum(username = "testing")
    }
}
