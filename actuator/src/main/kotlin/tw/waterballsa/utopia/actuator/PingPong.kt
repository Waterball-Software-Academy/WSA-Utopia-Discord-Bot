package tw.waterballsa.utopia.actuator

import mu.KotlinLogging
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * @author timm
 */

private const val PING_COMMAND_NAME = "ping"

@Component
class PingPong(private val pingPongRepository: MongoCollection<PingPongDocument, String>) : UtopiaListener() {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(PING_COMMAND_NAME, "sends pong")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (!isPingCommand()) {
                return
            }
            reply("pong").setEphemeral(true).queue() {
                val userId = member?.id ?: return@queue

                val pingPong = pingPongRepository.findOne(userId)?.let { pingPong ->
                    pingPong.created = now()
                    pingPongRepository.save(pingPong)
                } ?: run {
                    pingPongRepository.save(PingPongDocument(userId, now()))
                }

                log.info() { """[pingPong Document] { userId : ${pingPong.userId}, time : ${pingPong.created} }""" }
            }
        }
    }

    private fun SlashCommandInteractionEvent.isPingCommand(): Boolean {
        return PING_COMMAND_NAME == this.fullCommandName
    }

    private fun now() : Long{
        val currentDateTime = LocalDateTime.now()
        return currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
    }
}
