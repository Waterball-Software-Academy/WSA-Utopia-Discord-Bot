package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener
import tw.waterballsa.utopia.jda.log
import java.lang.System.lineSeparator
import java.nio.file.Files.*
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.APPEND
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private val studyCircleEventIds = mutableListOf<String>()
private val timer = Timer()
private const val TIMEZONE_ID = "Asia/Taipei"
private const val DATABASE_PATH = "data/gaas"
private const val DATAFILE_NAME_TEMPLATE = "/study-circle-absence-record-\$date.db"
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


fun collectStudyCircleEvent(wsaDiscordProperties: WsaDiscordProperties) = listener {
    on<ScheduledEventCreateEvent> {
        val partyChannelId = wsaDiscordProperties.wasPartyChannelId
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") && it.channel?.id == partyChannelId }
            ?.run {
                studyCircleEventIds.add(id)
                log.info { "Successfully collect the event of study circle, event id: $id" }
            }
    }
}

fun recordAttendanceStatus() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isStudyCircleEvent() && it.status == ScheduledEvent.Status.ACTIVE }
            ?.run {
                log.info { "Event start, event id: $id" }
                recordTask(this)
            }
    }
}

fun removeCanceledOrCompletedEvent() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isStudyCircleEvent() && (it.status == ScheduledEvent.Status.CANCELED || it.status == ScheduledEvent.Status.COMPLETED) }
            ?.run {
                log.info { "Remove cancel or completed event, event id: $id" }
                studyCircleEventIds.remove(id)
            }
    }
}

//活動開始時，判斷是否是 GaaS 讀書會，是則啟動 task，每隔一段時間檢查參加者名單並記錄
fun recordTask(event: ScheduledEvent) {
    val filePath = createDataFile()

    val now = getTaipeiCurrentDateTime()
    val start = now.withHour(21).withMinute(0).withSecond(0)
    val end = now.withHour(22).withMinute(0).withSecond(0)
    val participantCount = mutableListOf<Int>()

    timer.schedule(object : TimerTask() {
        override fun run() {
            val eventChannel = event.channel
            checkNotNull(eventChannel)

            eventChannel.asVoiceChannel().run {
                participantCount.add(members.size)
                val newContent = members.map { "${it.nickname} : ${it.id}" }
                val currentTime = dateFormatter.format(getTaipeiCurrentDateTime())
                writeParticipantsIntoFile(currentTime, newContent, filePath)
            }
        }
    }, getTaipeiTime(start), 10.seconds.inWholeMilliseconds)

    timer.schedule(
        object : TimerTask() {
            override fun run() {
                writeStaticsSummaryIntoFile(participantCount, filePath)
                timer.cancel()
                log.info { "Record Task has been closed." }
            }
        },
        getTaipeiTime(end)
    )
}

@Synchronized
private fun writeStaticsSummaryIntoFile(participantCount: List<Int>, filePath: Path) {
    val avgStatics = "Avg: ${participantCount.average().roundToInt()}"
    val maxStatics = "Max: ${participantCount.max()}"
    writeString(filePath, avgStatics + lineSeparator(), APPEND)
    writeString(filePath, maxStatics + lineSeparator(), APPEND)
}

@Synchronized
private fun writeParticipantsIntoFile(currentTime: String, newContent: List<String>, filePath: Path) {
    writeString(filePath, currentTime + lineSeparator(), APPEND)
    newContent.forEach { writeString(filePath, it + lineSeparator(), APPEND) }
    writeString(filePath, lineSeparator(), APPEND)
}

private fun createDataFile(): Path {
    val fileName = DATAFILE_NAME_TEMPLATE.replace("\$date", getTaipeiCurrentDate().toString())
    val filePath = Paths.get(DATABASE_PATH + fileName)
    val file = filePath.toFile()

    if (!file.exists()) {
        log.info { "Data File Created: ${file.name}" }
        createDirectories(Paths.get(DATABASE_PATH))
        createFile(filePath)
    }
    return filePath
}

private fun getTaipeiCurrentDateTime(): LocalDateTime = LocalDateTime.now(ZoneId.of(TIMEZONE_ID))

private fun getTaipeiCurrentDate(): LocalDate = LocalDate.now(ZoneId.of(TIMEZONE_ID))

private fun ScheduledEvent.isStudyCircleEvent() = id in studyCircleEventIds

private fun getTaipeiTime(start: LocalDateTime): Date =
    Date.from(start.atZone(ZoneId.of(TIMEZONE_ID)).toInstant())
