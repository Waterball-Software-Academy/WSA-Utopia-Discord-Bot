package tw.waterballsa.alpha.wsabot.domain

class TwoSum(var a: Int, var b: Int) {
    fun sum(username: String): String {
        return "$username \n$a + $b = ${a + b}"
    }
}
