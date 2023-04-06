package tw.waterballsa.utopia.guessnum1a2b

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import tw.waterballsa.utopia.guessnum1a2b.domain.GuessNum1A2B
import tw.waterballsa.utopia.guessnum1a2b.domain.generateSecretNumber
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.minutes

class GameRoom(val member: Member, val threadChannel: ThreadChannel) {
    private val game = GuessNum1A2B(generateSecretNumber())

    init {
        timer.schedule(timerTask {
            threadChannel.sendMessage("已經超過10分鐘").queue()
            repository.unregister(member, threadChannel)
        }, 10.minutes.inWholeMilliseconds)
    }

    fun guess(number: String): String {

        when {
            validateMessage(number) -> return "你的答案不是4個數字"
            game.isGuessedRight() -> return "遊戲已結束"
        }

        val result = game.guess(number)

        if (game.isGuessedRight()) {
            repository.unregister(member, threadChannel)
            return "$result 恭喜猜對!!"
        }

        return result
    }

    fun close() {
        threadChannel.run {
            sendMessage("遊戲房間將於10秒後關閉").queueAfter(1, TimeUnit.SECONDS)
            retrieveParentMessage().queueAfter(10, TimeUnit.SECONDS) { startMessage ->
                startMessage.delete().queue()
                threadChannel.delete().queue()
            }
        }
    }
}
