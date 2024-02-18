package tw.waterballsa.utopia.rockpaperscissors.domain

class RockPaperScissors(myPunch: Punch? = null, enemyPunch: Punch? = null) {
    fun punch(myPunch: Punch, enemyPunch: Punch): PunchResult {
        return when {
            // 剪刀->石頭->布，後者贏前者
            // 布的下一位要回到剪刀，所以依照拳種類數量取mod
            (myPunch.ordinal + 1).mod(Punch.values().size) == enemyPunch.ordinal -> PunchResult.LOSE
            (enemyPunch.ordinal + 1).mod(Punch.values().size) == myPunch.ordinal -> PunchResult.WIN
            else -> PunchResult.EVEN
        }
    }
}

enum class Punch(val icon: String) {
    SCISSORS("✌️剪刀"),
    ROCK("✊石頭"),
    PAPER("✋布");

    companion object;

    override fun toString(): String = icon
}

enum class PunchResult(private val message: String) {
    WIN("\uD83C\uDDFC \uD83C\uDDEE \uD83C\uDDF3 你贏了!!!"),
    LOSE("\uD83C\uDDF1 \uD83C\uDDF4 \uD83C\uDDF8 \uD83C\uDDEA 你輸了!!!"),
    EVEN("\uD83C\uDDEA \uD83C\uDDFB \uD83C\uDDEA \uD83C\uDDF3 平手~");

    companion object;

    override fun toString(): String = message
}
