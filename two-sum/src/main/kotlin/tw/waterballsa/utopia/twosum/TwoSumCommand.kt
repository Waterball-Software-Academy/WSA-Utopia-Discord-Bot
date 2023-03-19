package tw.waterballsa.utopia.twosum

import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands
import tw.waterballsa.utopia.twosum.app.TwoSumUseCase

fun twoSum() = commands("two-sum") {
    slash("two-sum", "Add two numbers together.") {
        execute(IntegerArg("First"), IntegerArg("Second")) {
            val (first, second) = args
            val twoSumUseCase: TwoSumUseCase = TwoSumUseCase()
            respond(twoSumUseCase.sum(first, second))
        }
    }
}
