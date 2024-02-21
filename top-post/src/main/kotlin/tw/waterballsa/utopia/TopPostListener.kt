package tw.waterballsa.utopia.toppost

import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener

const val likeEmoji = "\uD83D\uDC4D"


@Component
class TopPostListener(private val wsa: WsaDiscordProperties) : UtopiaListener() {

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        with (event) {
            val post = channel.asThreadChannel()
            val forum = post.parentChannel
            val hasPostLikeEmoji = emoji.name == likeEmoji

            if (forum.isTopicPoolForum() && hasPostLikeEmoji) {
                channel.sendMessage(likeEmoji).queue {
                    it.delete().queue()
                }
            }
        }
    }

    private fun IThreadContainerUnion.isTopicPoolForum() = id == wsa.topicPondChannelId

}
