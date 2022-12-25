package com.example.demo

import dev.kord.common.entity.*
import dev.kord.common.serialization.DurationInDays
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel

import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder.Limits.title
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.route.Route
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.Emojis.a
import dev.kord.x.emoji.Emojis.b
import dev.kord.x.emoji.Emojis.id
import dev.kord.x.emoji.Emojis.m
import dev.kord.x.emoji.Emojis.new
import jdk.jfr.Timestamp
import kotlinx.coroutines.Delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import me.jakejmattson.discordkt.Discord

import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.conversations.conversation
import me.jakejmattson.discordkt.conversations.responders.ChannelResponder
import me.jakejmattson.discordkt.dsl.*
import me.jakejmattson.discordkt.extensions.DiscordRegex.user
import me.jakejmattson.discordkt.extensions.TimeStamp
import me.jakejmattson.discordkt.extensions.button
import me.jakejmattson.discordkt.extensions.createMenu
import me.jakejmattson.discordkt.extensions.footer
import me.jakejmattson.discordkt.prompts.promptModal
import java.util.*
import javax.swing.event.ChangeEvent
import kotlin.coroutines.createCoroutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds



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
                        respondEphemeral {  }
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
            playerRank.merge(player.userid, 5, Int::plus)
            rankstr += "${player.userid} 獲得 ${playerRank[player.userid]} 分\n"
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

fun demo() = commands("Demo") {
    slash("run","開始遊戲" ) {
        execute() {
            game = Game(channel)
            game!!.CreateRanking()
            val c = game!!.CreateMenu(discord)


            c.createMessage("等待5秒後開始")
            delay(5000)
            while (!game!!.IsEndGame())
            {
                c.createMessage("開始下一提")
                game!!.Next()
                delay(10500)
                c.createMessage("答題結束 等待兩秒開始")
                game!!.UpdateRanking()
                delay(2000)
            }
            c.createMessage("遊戲結束!!")

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
            game!!.Next()
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
    on<MessageCreateEvent> {
        if(message.channel.asChannel().data.name.value == "全民軟體知識王"){
            if(message.author!!.isBot && message.author!!.username == "infinite") {

            }

        }
    }
}
