package tw.waterballsa.alpha.wsabot.bot.twosum.command

import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.User
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands
import tw.waterballsa.alpha.wsabot.app.TwoSumUseCase

fun twoSum() = commands("two-sum") {
    slash("two-sum", "Add two numbers together.") {
        execute(IntegerArg("First"), IntegerArg("Second")) {
            val (first, second) = args
            val twoSumUseCase: TwoSumUseCase = TwoSumUseCase()
            respond(twoSumUseCase.sum(first, second, author))
        }
    }
}
