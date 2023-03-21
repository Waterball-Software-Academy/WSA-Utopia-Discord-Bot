package tw.waterballsa.utopia.twosum

import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands
import mu.KotlinLogging
import tw.waterballsa.utopia.twosum.app.TwoSumUseCase

val log = KotlinLogging.logger {}

fun twoSum(usecase: TwoSumUseCase) = commands("two-sum") {
    slash("two-sum", "Add two numbers together.") {
        execute(IntegerArg("First"), IntegerArg("Second")) {
            val (first, second) = args
            log.info { "[Two Sum] $first + $second" }
            respond(usecase.sum(first, second))
        }
    }
}
