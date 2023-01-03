package tw.waterballsa.alpha.wsabot.gaas.commands

import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.optional.value
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.event.guild.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.toJavaInstant
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging
import java.time.ZoneOffset
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val logger = KotlinLogging.logger {}
val gaasEvents = mutableListOf<GuildScheduledEvent>()

fun catchGaasEvent() = listeners {
    on<GuildScheduledEventCreateEvent> {
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") }
            ?.let { gaasEvents.add(it) }
            .also { logger.info { "[${scheduledEvent.name}] has been added into GaaS Event lists, and its id is [${scheduledEvent.id}]" } }

        logger.info { "Create: ${scheduledEvent.scheduledStartTime}" }

    }
}

@OptIn(DelicateCoroutinesApi::class)
fun scheduledEventUpdateListeners() = listeners {
    on<GuildScheduledEventUpdateEvent> {
        scheduledEvent
            .takeIf { gaasEvents.haveThisEvent(it) && it.status == GuildScheduledEventStatus.Active }
            ?.let { event ->
                scheduleTaskAtSpecifyHour (event.getStartHour()) {
                    val eventChannel = event.channelId?.let { event.getGuild().getChannel(it) }

                    eventChannel ?.let {
                        logger.info { "[活動頻道]: ${it.name}" }
                        logger.info { "[成員人數]: ${it.data.memberCount.orElse(-100)}" }
                        logger.info { "[成員]: ${it.data.member}" }
                        logger.info { "[接收者]: ${it.data.recipients.value}" }
                        logger.info { "[接收者人數]: ${it.data.recipients.value?.size}" }
                    }
                }
            }
    }
}

fun removeAbandonedEvent() = listeners {
    on<GuildScheduledEventDeleteEvent> {
        scheduledEvent
            .let { event -> gaasEvents.removeIf { gaasEvents.haveThisEvent(event) } }
            .also {
                logger.info { "[${scheduledEvent.name}] has been removed from GaaS Event lists, and its id is [${scheduledEvent.id}]" } }
    }
}

private fun List<GuildScheduledEvent>.haveThisEvent(event: GuildScheduledEvent): Boolean =
    any { it.id.value == event.id.value }

private fun GuildScheduledEvent.getStartHour(): Int =
    scheduledStartTime.toJavaInstant().atZone(ZoneOffset.ofHours(8)).hour


@OptIn(DelicateCoroutinesApi::class)
fun scheduleTaskAtSpecifyHour(hour: Int, task: suspend () -> Unit) {

    GlobalScope.launch {
        val startHour = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Taipei")
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val endHour = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Taipei")
            set(Calendar.HOUR_OF_DAY, hour + 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val oneMinute = 60.toDuration(DurationUnit.SECONDS)

        with(Calendar.getInstance()) {
            timeZone = TimeZone.getTimeZone("Asia/Taipei")
            while (this in startHour..endHour) {
                println("Current: ${this.time}")
                println("startHour: ${startHour.time}")
                println("endHour: ${endHour.time}")
                println("Duration: ${oneMinute.inWholeSeconds}")
                logger.info { "Bot collection data..." }
                task()
                delay(oneMinute)
            }
        }
    }
}

