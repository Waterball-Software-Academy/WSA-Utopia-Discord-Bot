package tw.waterballsa.utopia.contactcenter

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener


private val chitChatChannelId = "1038657903437037590"
private var messageAmount = 0
private val prohibitEmojiId = "1138269084207825028" // prod 1058324156661379102, beta 1138269084207825028
private val prohibitEmojiName = "prohibit"
private val alphaBufferChannelId = "1138342435886465134"
private val alphaBufferRoleId = "1040676400413286400" // prod 991257315699335209 beta 1040676400413286400

@Component
class ContactCenterListener(
    private val properties: WsaDiscordProperties,
    private val wsaGuild: Guild
) : UtopiaListener() {

    //DONE 功能1:自動化排程訊息(每兩一次在特定語音頻道)
    //TODO 功能2:收到訊息的回覆自動化回復及給予buffer的回饋 (比如收到私訓的檢舉訊息就在 Buffer頻道內告知Buffer)
    //DONE 功能3:檢舉Emoji追蹤
    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (channel != wsaGuild.getTextChannelById(chitChatChannelId) || author.isBot) {
                return
            }
            postGentleArticle(event)
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val redDecimal = 16711680
        with(event) {
            if (emoji.name == prohibitEmojiName) {
                jda.getTextChannelById(alphaBufferChannelId)!!
                    .sendMessage(jda.getRoleById(alphaBufferRoleId)!!.asMention)
                    .addEmbeds(
                        Embed {
                            title = "收到檢舉！"
                            description = "喵球目前收到了一件客服唷！火速支援！".trimIndent()
                            color = redDecimal
                            field {
                                name = "訊息連結🔗"
                                value = "https://discord.com/channels/${properties.guildId}/${channel.id}/${messageId}"
                            }
                        }
                    ).queue()
            }
        }
    }
}

private fun postGentleArticle(messageReceivedEvent: MessageReceivedEvent) {
    if (messageAmount < 3) {
        messageAmount += 1
    }
    if (messageAmount == 3) {
        messageReceivedEvent.channel.sendMessage("這是紳士文").queue()
        messageAmount = 0
    }
}
