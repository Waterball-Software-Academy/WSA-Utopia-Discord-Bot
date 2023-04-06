package tw.waterballsa.utopia.knowledgeking

import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import net.dv8tion.jda.api.utils.Result.defer
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener


val logger = KotlinLogging.logger {}
fun Knowledgeking(wsa: WsaDiscordProperties, jda: JDA) = listener{
    on<ReadyEvent> {
        val guild = jda.getGuildById(wsa.guildId)!!
        val channel = guild.getTextChannelById(1039363776497061930)!!
        channel.sendMessage("KingoftheQuiz Started")
            .queue{
            logger.info { "[ReadyEvent] {\"name\" : \"KingoftheQuiz Started\"}" }
        }
        channel.sendMessageEmbeds(
            EmbedBuilder()
                .setTitle("one page")
                .build()
        ).addActionRow(
            Button.primary("1","1"),
            Button.primary("2","2"),
            Button.primary("3","3"),
            Button.primary("4","4")
        ).queue{
            logger.info { "[Build Button]" }
        }



    }

    on<ButtonInteractionEvent>{
        message.editMessageEmbeds(EmbedBuilder().setTitle("hi").build())
        reply("").queue()

        return@on
    }
}
        /*
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
            c.createMessage("知識王比賽已準備好 10秒後開始答題")捷運
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

         */



