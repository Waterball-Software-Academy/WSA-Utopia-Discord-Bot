package tw.waterballsa.alpha.wsabot.knowledgeking.commands

import me.jakejmattson.discordkt.commands.commands

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
    //main test
}