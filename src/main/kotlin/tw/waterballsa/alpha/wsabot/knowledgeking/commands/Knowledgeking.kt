package tw.waterballsa.alpha.wsabot.knowledgeking.commands

import me.jakejmattson.discordkt.commands.commands

val token = "MTAxMjM3NzI3MzI2NzE0NjgxMg.G_Ges8.l_00dt08GLKeBDCei2XUHpU8XScuSi-t-HbBro"
bot(token) {
    prefix { "+" }
    demo()
    demoListeners()
}
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

fun knowledgeking() = commands("knowledgeking") {
    slash("knowledgeking", "A 'knowledgeking' command.") {
        execute {
            respond("Start knowledgeking!")
            val mid = channel.createMenu {
                page {
                    title = "題目1"
                    description = "敘述1"
                }
                page {
                    title = "題目二"
                    description = "敘述二"

                }
                buttons {
                    actionButton("選項一", Emojis.regionalIndicatorA, ButtonStyle.Primary) {
                        channel.createMessage("玩家 ${user.username} ${user.id} 選擇第一個選項")

                    }
                    actionButton("選項二", Emojis.regionalIndicatorA, ButtonStyle.Primary) {
                        channel.createMessage("玩家${user.username} ${user.id} 選擇第二個選項")

                    }
                    button("上一題", Emojis.regionalIndicatorA, ButtonStyle.Primary) {
                        previousPage()
                        return@button
                    }
                    button("下一題", Emojis.regionalIndicatorA, ButtonStyle.Primary) {
                        nextPage()
                        return@button
                    }

                }
            }.id
            discord.kord.rest.channel.startThreadWithMessage(
                channel.id,
                mid,
                name = "123",
                ArchiveDuration.Day
            ) {}


            //channel.getLastMessage()!!.addReaction(Emojis.a)
        }
    }

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
    //main test
}