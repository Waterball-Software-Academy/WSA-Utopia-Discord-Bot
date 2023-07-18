package tw.waterballsa.utopia.utopiagamificationquest.extensions

import dev.minn.jda.ktx.interactions.components.button
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.QuizButton
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton
import java.util.Properties

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

val Mission.quizButton: Button
    get() = button(
        QuizButton.id(),
        QuizButton.LABEL
    )

