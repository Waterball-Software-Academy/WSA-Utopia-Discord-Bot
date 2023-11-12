package tw.waterballsa.utopia.utopiagamification.quest.extensions

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.User
import tw.waterballsa.utopia.utopiagamification.quest.listeners.RewardButton
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.AssignPlayerQuestPresenter
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.PlayerFulfillMissionPresenter
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun String.toDate(): LocalDateTime = LocalDateTime.parse(this)

fun OffsetDateTime.toTaipeiLocalDateTime(): LocalDateTime =
    atZoneSameInstant(ZoneId.of("Asia/Taipei")).toLocalDateTime()

class Color {
    companion object {
        const val GREEN = 706146
    }
}

fun AssignPlayerQuestPresenter.ViewModel.publishToUser(user: User) {
    user.openPrivateChannel().queue {
        it.sendMessageEmbeds(
            Embed {
                title = questTitle
                description = questDescription
                color = Color.GREEN

                field {
                    name = "任務條件"
                    value = criteria
                }

                field {
                    name = "任務位置"
                    value = link
                    inline = true
                }
            }
        ).queue()
    }
}

fun PlayerFulfillMissionPresenter.ViewModel.publishToUser(user: User) {
    user.openPrivateChannel().queue {
        it.sendMessage(postMessage)
            .addActionRow(RewardButton.toJdaButton(questId))
            .queue()
    }
}
