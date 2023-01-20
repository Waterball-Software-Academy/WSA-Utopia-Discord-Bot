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
val gaasEvents = HashMap<ULong, Boolean>() // <event, isStartLogging?>

//遊戲微服務
//val partyChannelId = (992631653954502716).toULong() // WSA - 學院節目區
val partyChannelId = (1040672621609619596).toULong() // beta - party

fun catchGaasEvent() = listeners {
    on<GuildScheduledEventCreateEvent> {
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") }
            ?.run { gaasEvents.putIfAbsent(id.value, false) }
            .also {
                logger.info { "[${scheduledEvent.name}] 已經加入 GaaS Event 清單，它的 ID 是 [${scheduledEvent.id}]" }
                logger.info { "活動建立時間：${scheduledEvent.scheduledStartTime}" }
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
                .takeIf {
                    gaasEvents.contains(it.id.value)
                }
                ?.run {
                    if (!gaasEvents[id.value]!! && status == GuildScheduledEventStatus.Active) {
                        scheduleTaskAtSpecifyHour(getStartHour()) {
                            val membersInEventChannel =
                                getGuild().members
                                    .filter { it.getVoiceStateOrNull()?.channelId?.value == partyChannelId }
                                    .map { it.fullName }
                                    .toList()

                            logger.info { "活動頻道成員列表：$membersInEventChannel" }
                            logger.info { "活動頻道成員數量：${membersInEventChannel.size}" }
                        }
                        gaasEvents[id.value] = true
                    }
                }
        }
    }

private fun GuildScheduledEvent.getStartHour(): Int =
    scheduledStartTime.toJavaInstant().atZone(ZoneOffset.ofHours(8)).hour

fun removeAbandonedEvent() =
    listeners {
        on<GuildScheduledEventDeleteEvent> {
            scheduledEvent.run {
                if (gaasEvents.contains(id.value)) {
                    gaasEvents.remove(id.value)
                    logger.info {
                        "[$name] 已經被刪除並從 GaaS Event 列表移除，它的 ID 是 [$id]"
                    }
                }
            }
        }
    }

fun removeCompletedEvent() =
    listeners {
        on<GuildScheduledEventUpdateEvent> {
            scheduledEvent
                .takeIf { gaasEvents.contains(it.id.value) }
                ?.run {
                    if (gaasEvents[id.value]!! && status == GuildScheduledEventStatus.Completed) {
                        gaasEvents.remove(id.value)
                        logger.info {
                            "[$name] 已經圓滿結束並從 GaaS Event 列表移除，它的 ID 是 [$id]"
                        }
                    }
                }
        }
    }

@OptIn(DelicateCoroutinesApi::class)
fun scheduleTaskAtSpecifyHour(hour: Int, task: suspend () -> Unit) =
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
                logger.info { "現在時間：${Calendar.getInstance().time}" }
                logger.info { "WaterGPT 蒐集資料中......" }
                task()
                delay(oneMinute)
            }
        }
    }


