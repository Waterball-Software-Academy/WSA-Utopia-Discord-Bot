package tw.waterballsa.utopia.usageinformation.daily

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.minigames.PlayerFinder
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.random.Random

private const val DAILY_COMMAND_NAME = "daily"

@Component
class DailyListener(
    val publisher: EventPublisher,
    val playerFinder: PlayerFinder
) : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(DAILY_COMMAND_NAME, "daily bounty")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            // 冷卻時間 24 小時
            // 你可以每天一次使用 /daily 指令，獎勵的範圍從 50 :coin: 到 120 :coin:。
            // 連續幾天要求獲得連勝紅利（連續 7 天 x4 倍）
            if (fullCommandName != DAILY_COMMAND_NAME) {
                return
            }

            // bounty
            val minReward = 50
            val maxReward = 120
            val reward = Random.nextInt(minReward, maxReward)
            val playerBounty = playerFinder.findById(user.id)!!.bounty
            var playerBountyResult = 0

            // sign in time
            var continuousSignInDays = playerFinder.findById(user.id)?.continuousSignInDays!!
            var lastSignInTime = playerFinder.findById(user.id)?.lastSignInTime
            if (lastSignInTime == null) {
                lastSignInTime = OffsetDateTime.now().minus(Duration.ofHours(24))
            }
            val currentTime = OffsetDateTime.now()
            val duration = Duration.between(lastSignInTime, currentTime)
            val isOver24Hours = duration.toHours() >= 24

            if (isOver24Hours) {
                playerBountyResult = if (continuousSignInDays % 7 == 0) {
                    reward * 4
                } else {
                    reward
                }
                continuousSignInDays += 1
                publisher.broadcastEvent(SignInEvent(user.id, playerBountyResult, currentTime, continuousSignInDays))
                reply("簽到成功！距離下次簽到還有 ${24 - duration.toHours()} 小時").setEphemeral(true).queue()
            } else {
                reply("你距離下次簽到還有 ${24 - duration.toHours()} 小時喔").setEphemeral(true).queue()
            }

        }
    }
}
