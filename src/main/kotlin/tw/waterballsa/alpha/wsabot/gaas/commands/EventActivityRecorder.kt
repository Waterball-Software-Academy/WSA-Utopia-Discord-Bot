package tw.waterballsa.alpha.wsabot.gaas.commands

import dev.kord.common.entity.GuildScheduledEventStatus
import dev.kord.common.entity.optional.value
import dev.kord.core.entity.GuildScheduledEvent
import dev.kord.core.event.guild.*
import io.ktor.util.date.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.toJavaInstant
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.fullName
import mu.KotlinLogging
import java.time.ZoneOffset
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val logger = KotlinLogging.logger {}
val gaasEvents = mutableListOf<GuildScheduledEvent>()

//val betaConferenceRoom = (1039191460408463421).toULong() // beta - beta conf 遊戲微服務 200264140512690176
val partyChannelId = (1040672621609619596).toULong() // beta - party

fun catchGaasEvent() = listeners {
    on<GuildScheduledEventCreateEvent> {
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") }
            ?.let { gaasEvents.add(it) }
            .also {
                logger.info { "[${scheduledEvent.name}] 已經加入 GaaS Event 清單, 它的 ID 是 [${scheduledEvent.id}]" }
                logger.info { "活動建立時間: ${scheduledEvent.scheduledStartTime}" }
            }
    }
}

fun scheduledEventUpdateListeners() =
    /*
    * 要取得 member 所在的 channel 必須使用 getVoiceStateOrNull 方法
    * 然而要更新 VoiceState 物件，Bot 必須要有能力接收 VoiceStateUpdateEvent
    * 故，請記得再 Bot Intent Config 加上 Intent.GuildVoiceStates，才能及時更新
    * */

    listeners {
        on<GuildScheduledEventUpdateEvent> {
            scheduledEvent
                .takeIf { gaasEvents.haveThisEvent(it) && it.status == GuildScheduledEventStatus.Active }
                ?.let { event ->
                    scheduleTaskAtSpecifyHour(event.getStartHour()) {
                        val membersInEventChannel =
                            event.getGuild()
                                .members
                                .filter { it.getVoiceStateOrNull()?.channelId?.value == partyChannelId }
                                .map { it.fullName }
                                .toList()
                        logger.info { "活動頻道成員列表: $membersInEventChannel" }
                        logger.info { "活動頻道成員數量: ${membersInEventChannel.size}" }
                    }
                }
        }
    }

fun removeAbandonedEvent() =
    listeners {
        on<GuildScheduledEventDeleteEvent> {
            scheduledEvent
                .let { event -> gaasEvents.removeIf { gaasEvents.haveThisEvent(event) } }
                .also {
                    logger.info { "[${scheduledEvent.name}] 已經從 GaaS Event 列表移除, 它的 ID 是 [${scheduledEvent.id}]" }
                }
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
            while (Calendar.getInstance() in startHour..endHour) {
                logger.info { "CurrentTime: ${Calendar.getInstance().time}" }
                logger.info { "Bot collection data..." }
                task()
                delay(oneMinute)
            }
        }
    }
}

