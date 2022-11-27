package com.example.demo

import dev.kord.common.entity.*
import dev.kord.common.serialization.DurationInDays
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.Message

import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder.Limits.title
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.Emojis.id
import dev.kord.x.emoji.Emojis.new
import jdk.jfr.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json

import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.*
import me.jakejmattson.discordkt.extensions.TimeStamp
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun main(args: Array<String>) {
    //val token = "MTAxMjM3NDg5MjA2NDI5MjkzNA.GbZORA.beJuKtGNe-q891GZ0R3z41ICMY2_JWidN7zX-M"
    val token = "MTAxMjM3NzI3MzI2NzE0NjgxMg.G_Ges8.l_00dt08GLKeBDCei2XUHpU8XScuSi-t-HbBro"
    bot(token) {
        prefix { "+" }
        demo()
        demoListeners()
    }
    Date().time

}


//1 1 2 3 5 8 13

val referee = KingoftheQuiz()
val solutions = listOf<Solution>(
    Solution("1 == 2 的結果是?", listOf<String>("1 == 2","string","true","false"),4, 10),
    Solution("456", listOf<String>("5","6","7","8"),3, 10),
    Solution("789", listOf<String>("1","6","3","5"),3, 10),
    Solution("101", listOf<String>("1","2","3","4"),3, 10),
    Solution("888", listOf<String>("1","3","3","4"),3, 10)
)
var current = 0


var mymenu : Menu? = null
var mymessage : Message? = null

fun demo() = commands("Demo") {
    slash("run", ) {
        execute {


            mymenu = menu {
                var count = 0
                var c = listOf("A", "B", "C", "D")

                page {
                    title = "準備中..."
                }

                for (i in solutions) {
                    page {
                        title = "題目${++count}"
                        description = "${i.question}\n"

                        for (j in 0..3) {
                            description += c[j] + ". " + i.option[j] + '\n'
                        }
                    }
                }

                buttons {
                    var emo = listOf(
                        Emojis.regionalIndicatorA,
                        Emojis.regionalIndicatorB,
                        Emojis.regionalIndicatorC,
                        Emojis.regionalIndicatorD
                    )

                    for (i in 1..4) {
                        val num = i
                        actionButton("", emo[i - 1], ButtonStyle.Primary) {

                            val result = referee.RsponeAnswer(user.id.toString(), num)
                            this.respondEphemeral {
                                this.content = result
                            }
                        }
                    }

                }
            }
            channel.createMessage {
                content = "排行\n"+
                        "1.player\n" +
                        "2.player\n" +
                        "3.player\n" +
                        "4.player\n" +
                        "5.player\n"
            }
            mymessage = channel.createMessage("Loading...")
            mymessage!!.edit(mymenu!!)

            discord.kord.rest.channel.startThreadWithMessage(
                channel.id,
                mymessage!!.id,
                name = "123",
                ArchiveDuration.Day
            ) {}

            //channel.getLastMessage()!!.addReaction(Emojis.a)
        }
    }

    slash("printComptition")
    {
        execute {
            val table = referee.GetCompetition().GetAnswerList().table
            var content = String()
            val l = listOf("","A","B","C","D")
            for(i in table.keys)
            {
                content += "題目:${i.question} \n" +
                        "答案:(${i.correctanswer})  ${i.option[i.correctanswer-1]}\n"+
                        "responed player:\n"
                for(j in table[i]!!)
                {
                    content += "${j.userid} 選擇 ${l[j.useranswer]} 花 ${j.responetime} s\n "
                }
            }
            channel.createMessage(content)
        }
    }

    slash("restart") {
        execute {
            current = 0;
            referee.Restart()
            referee.Prepare(solutions[current], 10)
        }
    }

    slash("next") {
        execute {
            mymenu!!.nextPage()
            mymessage!!.edit(mymenu!!)
            referee.Prepare(solutions[current++], 10)
            return@execute
        }
    }

    slash("Add", "Add two numbers together.") {
        execute(IntegerArg("First"), IntegerArg("Second")) {
            val (first, second) = args
            respond(first + second)
        }
    }


}

fun demoListeners() = listeners {
    on<MessageCreateEvent> {
        if(message.channel.asChannel().data.name.value == "全民軟體知識王"){
            if(message.author!!.isBot && message.author!!.username == "infinite") {

            }

        }
    }
}
