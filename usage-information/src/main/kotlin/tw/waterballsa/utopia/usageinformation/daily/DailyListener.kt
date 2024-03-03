package tw.waterballsa.utopia.usageinformation.daily

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener

private const val DAILY_COMMAND_NAME = "daily"

@Component
class DailyListener : UtopiaListener() {
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
        }
    }
}
