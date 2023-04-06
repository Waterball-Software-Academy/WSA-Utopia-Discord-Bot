package tw.waterballsa.utopia.guessNum1A2B

import ch.qos.logback.core.util.OptionHelper.getEnv
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import tw.waterballsa.utopia.guessNum1A2B.domain.guessNum1A2B
import tw.waterballsa.utopia.guessNum1A2B.domain.generateSecretNumber
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener

val logger = KotlinLogging.logger {}
lateinit var guessNumberChannelId : String
var game = guessNum1A2B(generateSecretNumber())


fun GuessNum1A2BListerner(wsa: WsaDiscordProperties, jda: JDA) = listener {
    on<ReadyEvent> {
        if (getEnv("DEPLOYMENT_ENV") != "beta") return@on
        guessNumberChannelId = wsa.wsaGuessNumberChannelId

        val guild = jda.getGuildById(wsa.guildId)!!
        val channel = guild.getTextChannelById(guessNumberChannelId)!!

        channel.sendMessage("Guess Number Game 1A2B Started")
            .queue {
                logger.info { "[ReadyEvent] {\"name\" : \"Guess Number Game 1A2B Started\"}" }
            }
    }

    on<MessageReceivedEvent> {
        if (getEnv("DEPLOYMENT_ENV") != "beta") return@on
        if (channel.id != guessNumberChannelId || message.author.isBot) return@on
        logger.info { "[MessageReceivedEvent] {\"feature\" : \"guessgame1A2B\",\"content\" : \"${message.contentDisplay}\"}" }
        var result = game.guess(message.contentDisplay)
        if (result == "4A0B") {
            result += "\n恭喜猜對了!!\n重新開始新的遊戲:"
            game = guessNum1A2B(generateSecretNumber())
        }
        channel.sendMessage(result).queue {
            logger.info { "[MessageReceivedEvent] {\"feature\" : \"guessgame1A2B\",\"result\" : \"$result\"}" }
        }

    }

}
