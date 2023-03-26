package tw.waterballsa.utopia.twosum

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import tw.waterballsa.utopia.jda.listener

val log = KotlinLogging.logger {}

fun twoSum() = listener {
    on<MessageReceivedEvent> {
        log.info { "New message: $message" }
    }
    on<MessageDeleteEvent> {
        log.info { "Delete message: $messageId" }
    }
}