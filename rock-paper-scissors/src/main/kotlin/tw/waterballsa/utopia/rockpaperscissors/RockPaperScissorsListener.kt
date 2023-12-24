package tw.waterballsa.utopia.rockpaperscissors

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.minigames.MiniGamePlayer
import tw.waterballsa.utopia.minigames.PlayerFinder
import tw.waterballsa.utopia.minigames.UtopiaListenerImpl
import tw.waterballsa.utopia.rockpaperscissors.domain.Punch
import tw.waterballsa.utopia.rockpaperscissors.domain.PunchResult
import tw.waterballsa.utopia.rockpaperscissors.domain.RockPaperScissors

private const val ROCK_PAPER_SCISSORS_COMMAND = "rock"

@Component
class RockPaperScissorsListener(
    publisher: EventPublisher,
    playerFinder: PlayerFinder
) : UtopiaListenerImpl<RockPaperScissors>(publisher, playerFinder) {
    private val discordUserIdToMiniPlayerId = hashMapOf<String, String>()
    private val game = RockPaperScissors()

    override fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer) {
        registerGame(miniGamePlayer.id, game)
        discordUserIdToMiniPlayerId[user.id] = miniGamePlayer.id
        reply("請猜拳!").setEphemeral(true)
            .addActionRow(Punch.BUTTONS).queue()
    }

    override fun getCommandName(): String {
        return ROCK_PAPER_SCISSORS_COMMAND
    }

    override fun getCommandDescription(): String {
        return "Start a new rock paper scissor game."
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if (!isRockPaperScissorButtonId()) {
                return
            }

            val playerPunch = button.id!!.toPunch()
            val botPunch = Punch.randomPunch()
            val punchResult = game.punch(playerPunch, botPunch)
            val miniGamePlayer = findPlayer(user.id)
            var miniGamePlayerBet = findBet(user.id)

            if (punchResult == PunchResult.LOSE) {
                miniGamePlayerBet = 0u - miniGamePlayerBet
            }

            reply(
                """
                    ${member?.asMention}
                    猜拳結果如下..
                    🙋‍♂️玩家 -> $playerPunch
                    🖥️系統 -> $botPunch
                    ----------------
                    🎯結果 -> $punchResult
                """.trimIndent()
            ).queue {
                discordUserIdToMiniPlayerId[user.id]?.let { miniGamePlayerId -> unRegisterGame(miniGamePlayerId) }
                discordUserIdToMiniPlayerId[user.id]?.let { miniGamePlayerID ->
                    gameOver(
                        miniGamePlayerID,
                        miniGamePlayerBet
                    )
                }
            }
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
