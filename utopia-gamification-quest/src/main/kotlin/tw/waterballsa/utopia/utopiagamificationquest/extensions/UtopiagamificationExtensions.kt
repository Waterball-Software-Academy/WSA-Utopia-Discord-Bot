package tw.waterballsa.utopia.utopiagamificationquest.extensions

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton
import java.time.LocalDateTime

fun String.toDate(): LocalDateTime = LocalDateTime.parse(this)

fun User.claimMissionReward(mission: Mission) {
    openPrivateChannel().complete().publishReward(mission)
}

fun PrivateChannel.publishReward(mission: Mission) {
    with(mission) {
        sendMessage(quest.reward.respond)
            .addActionRow(rewardButton)
            .complete()
    }
}

val Mission.rewardButton: Button
    get() = button(
        RewardButton.id(quest.title),
        RewardButton.LABEL
    )


fun Mission.publishToUser(user: User): MessageCreateAction {
    val channel = user.openPrivateChannel().complete()

    return channel.sendMessageEmbeds(Embed {
        title = quest.title
        description = quest.description
    })
}
