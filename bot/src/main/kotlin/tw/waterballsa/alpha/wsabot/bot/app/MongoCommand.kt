package tw.waterballsa.alpha.wsabot.bot.app

import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.MessageArg
import me.jakejmattson.discordkt.commands.commands
import tw.waterballsa.alpha.wsabot.app.MongoTest

fun twoSum() = commands("two-sum") {
    slash("mongo-save", "save something into mongo") {
        execute(AnyArg("note")) {
            val (note) = args
            respond("id：${MongoTest().save(note = note)}")
        }
    }

    slash("mongo-query", "query something from mongo") {
        execute(AnyArg("id")) {
            val (id) = args
            respond(MongoTest().query(id))
        }
    }
}
