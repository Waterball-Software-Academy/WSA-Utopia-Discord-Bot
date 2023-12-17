package tw.waterballsa.utopia.toppost

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

const val goodEmoji = "\uD83D\uDC4D"


@Component
class TopPostListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val channel = event.channel
        val parentChannel = channel.asThreadChannel().parentChannel
        if (wsa.topicPondChannelId == parentChannel.id && goodEmoji == (event.emoji.name)) {
            channel.sendMessage(goodEmoji).queue {
                it.delete().queue()
            }
        }
    }

}
