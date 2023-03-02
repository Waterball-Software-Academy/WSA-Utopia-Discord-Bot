package tw.waterballsa.alpha.wsabot.app

import tw.waterballsa.alpha.wsabot.domain.TwoSum

class TwoSumUseCase {
    fun sum(a: Int, b: Int): Int {
        return TwoSum(a, b).sum()
    }
}
