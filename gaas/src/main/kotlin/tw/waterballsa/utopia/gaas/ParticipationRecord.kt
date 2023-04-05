package tw.waterballsa.utopia.gaas

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.utils.createDirectoryIfNotExists
import tw.waterballsa.utopia.commons.utils.createFileIfNotExists
import tw.waterballsa.utopia.jda.listener
import java.io.File
import java.lang.System.lineSeparator
import java.nio.file.Files.*
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

/*
* ParticipationRecord is one of the features in GaaS.
* It is used to record the attendance status for GaaS events.
* When an event starts, a periodic task is initiated to keep track of the participants.
* */

private val gaaSEventIds = mutableListOf<String>()
private val timer = Timer()
private const val DATABASE_DIRECTORY = "data/gaas"
private const val DATABASE_FILENAME_TEMPLATE = "/study-circle-absence-record-\$date.db"
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val log = KotlinLogging.logger {}

fun collectGaaSEvents(wsaDiscordProperties: WsaDiscordProperties) = listener {
    on<ScheduledEventCreateEvent> {
        val partyChannelId = wsaDiscordProperties.wsaPartyChannelId
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") && it.channel?.id == partyChannelId }
            ?.run {
                log.info { "[GaaSEventCreated] {\"message\":\"New GaaS event has been created and collected, event id: $id\"}"  }
                gaaSEventIds.add(id)
            }
    }
}

fun whenGaaSEventStartsThenRecordTheParticipationStatus() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isStudyCircleEvent() && it.status == ScheduledEvent.Status.ACTIVE }
            ?.run {
                log.info { "[RecordTaskStarted] {\"message\":\"Start recording task, event id: $id\"}"  }
                recordEventParticipationStats()
            }
    }
}

fun removeCanceledOrCompletedEvent() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isStudyCircleEvent() && (it.status == ScheduledEvent.Status.CANCELED || it.status == ScheduledEvent.Status.COMPLETED) }
            ?.run {
                log.info { "[RemoveCancelOrCompletedGaaSEvent] {\"message\":\"Remove cancel or completed event, event id: $id\"}"  }
                gaaSEventIds.remove(id)
            }
    }
}

/*
* When the event starts, it is checked whether it is a GaaS study group, and if so,
* a task is started to periodically check the list of participants and record their attendance.
* */
fun ScheduledEvent.recordEventParticipationStats() {
    val filePath = createDataFile()

    val now = getTaipeiCurrentDateTime()
    val start = now.withHour(21).withMinute(0).withSecond(0)
    val end = now.withHour(22).withMinute(0).withSecond(0)
    val participantCount = mutableListOf<Int>()

    timer.schedule(object : TimerTask() {
        override fun run() {
            val channel = channel
            checkNotNull(channel)
            channel.asVoiceChannel().run {
                participantCount.add(members.size)
                val newContent = members.map { "${it.nickname} : ${it.id}" }
                val currentTime = dateFormatter.format(getTaipeiCurrentDateTime())
                writeParticipantsIntoFile(currentTime, newContent, filePath)
            }
        }
    }, getTaipeiTime(start), 3.minutes.inWholeMilliseconds)

    timer.schedule(
        object : TimerTask() {
            override fun run() {
                writeStaticsSummaryIntoFile(participantCount, filePath)
                timer.cancel()
                log.info { "[RecordTaskFinished] {\"message\":\"Recording task has been finished.\"}" }
            }
        }, getTaipeiTime(end)
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
    val fileName = DATABASE_FILENAME_TEMPLATE.replace("\$date", getTaipeiCurrentDate().toString())
    File(DATABASE_DIRECTORY).createDirectoryIfNotExists()
    return File(DATABASE_DIRECTORY + fileName).createFileIfNotExists()
}

private fun getTaipeiCurrentDateTime(): LocalDateTime = LocalDateTime.now()

private fun getTaipeiCurrentDate(): LocalDate = LocalDate.now()

private fun ScheduledEvent.isStudyCircleEvent() = id in gaaSEventIds

private fun getTaipeiTime(localDateTime: LocalDateTime): Date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
