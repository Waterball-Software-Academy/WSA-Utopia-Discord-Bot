package tw.waterballsa.utopia.guessnum1a2b

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.guessnum1a2b.domain.GuessNum1A2B
import tw.waterballsa.utopia.guessnum1a2b.domain.correctAnswer
import tw.waterballsa.utopia.guessnum1a2b.domain.generateSecretNumber
import tw.waterballsa.utopia.jda.listener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.minutes

val logger = KotlinLogging.logger {}
internal val timer = Timer()
internal val repository = GameRepository()

fun guessNum1A2BListener(wsa: WsaDiscordProperties, jda: JDA) = listener {

    command {
        Commands.slash("play", "start your game")
            .addSubcommands(SubcommandData("1a2b", "guess num game"))
    }

    on<SlashCommandInteractionEvent> {
        if (this.fullCommandName != "play 1a2b") {
            return@on
        }

        member?.run {
            repository.find(this)?.let {
                reply("你已經有一間房間了").setEphemeral(true).queue()
            } ?: reply("1A2B遊戲開始! 將於10分鐘後關閉，$asMention 遊玩愉快~").queue {
                hook.retrieveOriginal().queue { message ->
                    message.createThreadChannel("$effectiveName's room").queue { threadChannel ->
                        repository.register(this, threadChannel)
                    }
                }
            }
        }
    }

    on<MessageReceivedEvent> {
        repository.find(member!!, channel.asThreadChannel())?.run {
            threadChannel.sendMessage(guess(message.contentDisplay)).queue()
        }
    }

}

fun validateMessage(message: String): Boolean {
    return message.length != 4 || !(message matches Regex("\\d+(\\.\\d+)?"))
}
