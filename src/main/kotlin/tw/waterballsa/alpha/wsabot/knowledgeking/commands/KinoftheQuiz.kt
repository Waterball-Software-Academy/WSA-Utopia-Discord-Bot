package tw.waterballsa.alpha.wsabot.knowledgeking.commands

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.value
import dev.kord.core.behavior.channel.*
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel

import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.delay
import me.jakejmattson.discordkt.Discord

import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.conversations.conversation
import me.jakejmattson.discordkt.dsl.*

import java.util.concurrent.TimeUnit

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.ln

fun scheduleTaskAtEightPM(task: suspend () -> Unit) {
    GlobalScope.launch {
        // 计算从当前时间到每天晚上 8 点之间的时间差
        //首先，使用 Calendar.getInstance() 创建了两个日历对象：now 和 eightPM。now 对象表示当前时间，eightPM 对象表示每天晚上 8 点。
        //
        //然后，使用 apply 函数设置了 eightPM 对象的时、分、秒字段，使其表示每天晚上 8 点。
        //
        //接着，使用 if 语句检查当前时间是否在每天晚上 8 点之后。如果是，则使用 add 函数将 eightPM 对象的日期字段加 1，以便表示第二天晚上 8 点。
        //
        //最后，使用 timeInMillis 属性计算出每天晚上 8 点的毫秒数，并使用 timeInMillis 属性计算出当前时间的毫秒数，并计算两者之差，得到从当前时间到每天晚上 8 点之间的时间差，存储在 delay 变量中。
        val now = Calendar.getInstance()
        val eightPM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (now.after(eightPM)) eightPM.add(Calendar.DATE, 1)
        val delay = eightPM.timeInMillis - now.timeInMillis

        // 等待时间差
        delay(delay)

        //
        while (true) {
            val nowtime = Calendar.getInstance()
            task()
            val endtime = Calendar.getInstance()
            val delaytime = endtime.timeInMillis - nowtime.timeInMillis
            delay(TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS) - delaytime)
        }
    }
}

class Game(_channel : MessageChannel)
{
    private var gamemenu : Menu? = null
    private var gamerank : Message? = null
    private var gamemessage : Message? = null
    private val kingoftheQuiz = KingoftheQuiz()
    private val channel = _channel
    private var solutions : List<Solution>? = null
    private var current = -1
    private var playerRank = mutableMapOf<String, Int>()
    suspend fun CreateMenu(dc: Discord) : MessageChannelBehavior
    {
        solutions = GooleSheet().ReadSolution()
        gamemessage = channel.createMessage("loading")
        var b = dc.kord.rest.channel.startThreadWithMessage(
            channel.id,
            gamemessage!!.id,
            name = "123",
            ArchiveDuration.Day
        ) {}.id

        val m = MessageChannelBehavior(b, dc.kord)
        gamemenu = menu {
            var count = 0
            var c = listOf("A", "B", "C", "D")

            page {
                title = "準備中..."
            }

            for (i in solutions!!) {
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

                        val result = kingoftheQuiz.RsponeAnswer(user.id.toString(), num)
                        respondEphemeral {
                            content = result
                        }
                        m.createMessage {
                            content = result
                        }
                    }
                }
            }
        }
        gamemessage!!.edit(gamemenu!!)


        return m
    }

    suspend fun CreateRanking()
    {
        gamerank = channel.createMessage("Rank:")
    }

    suspend fun UpdateRanking()
    {
        if (current == -1) return
        val playerdata = kingoftheQuiz.GetAnswerList()
        val currentsolution = solutions?.get(current)!!
        var rankstr = "Rnak:\n"

        for (player in playerdata.table[currentsolution]!!)
        {
            //(答對(1or0)*5+LN(1/答題時間)*2)*10
            val isCorrect = player.useranswer.compareTo(currentsolution.correctanswer)
            val timeweight = ln(1/player.responetime.toDouble())
            var score = (isCorrect * 5 - timeweight) * 10
            println("${player.useranswer}   \n ${currentsolution.correctanswer}   \n 得分${score.toInt()} ")
            if(score > 0) playerRank.merge(player.userid, score.toInt(), Int::plus)

        }

        for( p in playerRank.keys)
        {
            rankstr += "${p} 獲得 ${playerRank[p]} 分\n"
        }

        gamerank!!.edit {
            content = rankstr
        }
    }
    suspend fun Show()
    {
        val table = kingoftheQuiz.GetAnswerList().table
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
    fun IsEndGame() :  Boolean
    {
        return current+1 == solutions!!.size
    }
    suspend fun Next()
    {
        gamemenu!!.nextPage()
        gamemessage!!.edit(gamemenu!!)
        kingoftheQuiz.Prepare(solutions!![++current], 10)
    }
}

var game : Game? = null
//1 1 2 3 5 8 13

fun KingofQuizCommand() = commands("Demo") {
    slash("run","開始遊戲" ) {
        execute() {
            scheduleTaskAtEightPM {

                channel.createMessage("知識王比賽10分鐘後開始囉")
                delay(300000)
                channel.createMessage("知識王比賽5分鐘後開始囉")
                delay(300000)

                game = Game(channel)

                game!!.CreateRanking()
                val c = game!!.CreateMenu(discord)
                c.createMessage("知識王比賽已準備好 10秒後開始答題")
                delay(10000)

                while (!game!!.IsEndGame()) {
                    c.createMessage("開始下一提")
                    game!!.Next()
                    delay(10500)
                    c.createMessage("答題結束 等待兩秒開始")
                    game!!.UpdateRanking()
                    delay(2000)
                }
                c.createMessage("遊戲結束!!")

            }

            //channel.getLastMessage()!!.addReaction(Emojis.a)
        }
    }

    slash("printComptition")
    {
        execute {
            game!!.Show()
        }
    }

    slash("update") {
        execute {
            game!!.UpdateRanking()
            game!!.Next()
        }
    }



    slash("next") {
        execute {
            var i = VoiceChannelBehavior(Snowflake(1038654765896310804), Snowflake(1039191460408463421), discord.kord)

            channel.createMessage("aaa"+ i.asChannel().data.member.value)
            print(channel.data.messageCount.value)
            return@execute
        }
    }

    slash("Add", "Add two numbers together.") {
        execute(IntegerArg("First"), IntegerArg("Second")) {
            val (first, second) = args
            respond(first + second)
        }
    }

    slash("SendFrom", "Add two numbers together.") {
        execute {
            val mychannel = MessageChannelBehavior(Snowflake(1038665145687212114), discord.kord)

            conversation(exitString = "exit") {
                val age = this.promptButton<String> {  }
                respond(age)
            }.startPrivately(discord, author) //val result = demoConversation().startPublicly(discord, author, channel)

        }
    }

}

fun demoListeners() = listeners {


}
