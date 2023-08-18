package tw.waterballsa.utopia.utopiagamificationquest.extensions

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.User
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun String.toDate(): LocalDateTime = LocalDateTime.parse(this)

fun OffsetDateTime.toTaipeiLocalDateTime(): LocalDateTime =
    atZoneSameInstant(ZoneId.of("Asia/Taipei")).toLocalDateTime()


fun Mission.publishToUser(user: User) {
    user.openPrivateChannel().queue {
        it.sendMessageEmbeds(
            Embed {
                title = quest.title
                description = quest.description
                color = Color.GREEN

                field {
                    name = "任務條件"
                    //轉成 string 並去除多餘的換行
                    value = "${quest.criteria}".replace(Regex("\\n{2,}"), "\n")
                }

                field {
                    name = "任務位置"
                    value = quest.criteria.link
                    inline = true
                }
            }
        ).queue()
    }
}

class Color {
    companion object {
        val GREEN = 706146
    }
}
