package tw.waterballsa.utopia.minigames

import javassist.NotFoundException
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.jda.extensions.getOptionAsPositiveInt

//TODO:
// 1. player use command to start game and give money to bet.
// 2. get player and money.
// 3. check player has enough money to bet.
// 4. start game, input player bet.
// 5. end game return player bounty.
// 6. add bounty to player

private const val OPTION_AMOUNT = "amount"

@Component
abstract class UtopiaListenerImpl<T>(
    private val publisher: EventPublisher,
    private val playerFinder: PlayerFinder
) : UtopiaListener() {
    companion object {
        const val MAX_BET = 240u
    }

    open val playerIdToGame = hashMapOf<String, T>()
    open val playerBet = hashMapOf<String, UInt>()

    final override fun commands(): List<CommandData> = listOf(
        Commands.slash(getCommandName(), getCommandDescription())
            .addOption(OptionType.INTEGER, OPTION_AMOUNT, "請輸入下注金額。(最多只能下注 $MAX_BET)", true)
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != getCommandName()) {
                return
            }

            //TODO: 尚未註冊成遊戲玩家的成員如何處理
            val miniGamePlayer = findPlayer(player.id)
//            playerFinder.findById(player.id)
//                ?: throw NotFoundException("請先執行烏托邦任務。輸入指令：utopia-first-quest")

            if (validatePlayerBet(miniGamePlayer)) {
                startGame(miniGamePlayer)
            }
        }
    }

    protected abstract fun getCommandName(): String

    protected abstract fun getCommandDescription(): String

    fun findPlayer(playerId: String): MiniGamePlayer {
        return playerFinder.findById(playerId) ?: throw NotFoundException("請先執行烏托邦任務。輸入指令：utopia-first-quest")
    }

    fun findBet(playerId: String): UInt {
        return playerBet[playerId]!!
    }

    private fun SlashCommandInteractionEvent.validatePlayerBet(miniGamePlayer: MiniGamePlayer): Boolean {
        val bet = getOptionAsPositiveInt(OPTION_AMOUNT)!!.toUInt()

        if (bet > MAX_BET) {
            reply("${player.asMention}，下注金額不可超過 $MAX_BET 元。").queue()
            return false
        }

        if (miniGamePlayer.bounty < bet) {
            reply("${player.asMention}，你擁有的金幣不足。").queue()
            return false
        }
        playerBet[player.id] = bet
        return true
    }

    protected abstract fun SlashCommandInteractionEvent.startGame(miniGamePlayer: MiniGamePlayer)

    protected fun registerGame(playerId: String, game: T) {
        playerIdToGame[playerId] = game
    }

    protected fun unRegisterGame(playerId: String) {
        playerIdToGame.remove(playerId)
        playerBet.remove(playerId)
    }

    protected fun gameOver(playerId: String, bounty: Int) {
        publisher.broadcastEvent(GameSettledEvent(playerId, bounty))
    }

    private val SlashCommandInteractionEvent.player
        get() = member!!

    private val ButtonInteractionEvent.player
        get() = member!!
}
