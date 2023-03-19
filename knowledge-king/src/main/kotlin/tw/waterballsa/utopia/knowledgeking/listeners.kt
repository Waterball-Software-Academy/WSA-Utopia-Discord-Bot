package tw.waterballsa.utopia.knowledgeking

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.event.gateway.ReadyEvent
import kotlinx.coroutines.delay
import me.jakejmattson.discordkt.dsl.listeners

var game : Game? = null
fun Listener() = listeners {

    on<ReadyEvent> {
        val c = MessageChannelBehavior(Snowflake(1038657903437037590),discord.kord).asChannel();
        c.createMessage("知識王已啟動")

        scheduleTaskAtEightPM {

            c.createMessage("知識王比賽10分鐘後開始囉")
            delay(300000)
            c.createMessage("知識王比賽5分鐘後開始囉")
            delay(300000)

            game = Game(c)

            game!!.CreateRanking()
            val c = game!!.CreateMenu(discord)
            c.createMessage("知識王比賽已準備好 10秒後開始答題")
            delay(10000)

            while (!game!!.IsEndGame()) {
                c.createMessage("開始下一題")
                game!!.Next()
                delay(10500)
                c.createMessage("答題結束 等待兩秒開始")
                game!!.UpdateRanking()
                delay(2000)
            }
            c.createMessage("遊戲結束!!")
        }
    }


}
