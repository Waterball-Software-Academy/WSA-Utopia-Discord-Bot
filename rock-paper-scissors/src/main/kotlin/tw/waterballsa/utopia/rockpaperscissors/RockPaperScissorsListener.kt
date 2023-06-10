package tw.waterballsa.utopia.rockpaperscissors

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.rockpaperscissors.domain.Punch
import tw.waterballsa.utopia.rockpaperscissors.domain.RockPaperScissors

private const val ROCK_PAPER_SCISSORS_COMMAND = "rock-paper-scissors"

@Component
class RockPaperScissorsListener() : UtopiaListener() {

    private val game = RockPaperScissors()

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(ROCK_PAPER_SCISSORS_COMMAND, "start a new rock paper scissors game!")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != ROCK_PAPER_SCISSORS_COMMAND) {
                return
            }

            reply("請猜拳!").setEphemeral(true)
                .addActionRow(Punch.BUTTONS).queue()
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if (!isRockPaperScissorButtonId()) {
                return
            }

            val playerPunch = button.id!!.toPunch()
            val botPunch = Punch.randomPunch()
            val punchResult = game.punch(playerPunch, botPunch)

            reply(
                """
                    ${member?.asMention}
                    猜拳結果如下..
                    🙋‍♂️玩家 -> $playerPunch
                    🖥️系統 -> $botPunch
                    ----------------
                    🎯結果 -> $punchResult
                """.trimIndent()
            ).queue()
        }
    }
}

private val Punch.Companion.BUTTONS: List<Button>
    get() = Punch.values().map { Button.primary(it.toButtonId(), it.icon) }

private fun ButtonInteractionEvent.isRockPaperScissorButtonId(): Boolean =
    Punch.values().any { it.toButtonId() == button.id!! }

private fun Punch.toButtonId(): String = "$name-$ordinal"

private fun String.toPunch(): Punch = Punch.valueOf(split("-").first())

private fun Punch.Companion.randomPunch(): Punch = Punch.values().random()
