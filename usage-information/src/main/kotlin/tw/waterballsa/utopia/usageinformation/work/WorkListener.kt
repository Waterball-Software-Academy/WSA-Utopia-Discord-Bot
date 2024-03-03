package tw.waterballsa.utopia.usageinformation.work

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener


private const val WORK_COMMAND_NAME= "work"
private const val OPTION_NAME = "claim"

@Component
class WorkListener : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(WORK_COMMAND_NAME, "工作打卡")
                .addOptions(
                    OptionData(OptionType.STRING, OPTION_NAME, "", false)
                )
        )
    }
}
