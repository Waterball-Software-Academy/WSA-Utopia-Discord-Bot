package tw.waterballsa.alpha.wsabot.app

import dev.kord.core.entity.User
import tw.waterballsa.alpha.wsabot.domain.TwoSum

class TwoSumUseCase {
    fun sum(a: Int, b: Int, user: User): String {
        val twoSum: TwoSum = TwoSum(a, b)
        return twoSum.sum(username = user.username)
    }
}
