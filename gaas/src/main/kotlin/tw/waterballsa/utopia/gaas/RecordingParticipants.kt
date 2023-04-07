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
import java.nio.file.Files.writeString
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
* RecordingParticipants is one of the features in GaaS.
* It is used to record the attendance status for GaaS events.
* When an event starts, a periodic task is initiated to keep track of the participants.
* */

private val gaasEventIds = hashSetOf<String>()
private val timer = Timer()
private const val DATABASE_DIRECTORY = "data/gaas/participation-stats"
private const val DATABASE_FILENAME_PREFIX = "/study-circle-participants-record-"
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val log = KotlinLogging.logger {}

fun collectGaaSEventsOnEventCreated(wsaDiscordProperties: WsaDiscordProperties) = listener {
    on<ScheduledEventCreateEvent> {
        val partyChannelId = wsaDiscordProperties.wsaPartyChannelId
        scheduledEvent
            .takeIf { it.name.contains("遊戲微服務") && it.channel?.id == partyChannelId }
            ?.run {
                log.info { "[GaaSEventCreated] {\"message\":\"New GaaS event has been created and collected, event id: $id\"}" }
                gaasEventIds.add(id)
            }
    }
}

fun recordTheGaasMemberParticipationOnGaaSEventStarted() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isGaaSEvent() && it.status == ScheduledEvent.Status.ACTIVE }
            ?.run {
                log.info { "[RecordTaskStarted] {\"message\":\"Start recording task, event id: $id\"}" }
                recordEventParticipation()
            }
    }
}

fun removeGaaSEventOnCanceledOrCompleted() = listener {
    on<ScheduledEventUpdateStatusEvent> {
        scheduledEvent
            .takeIf { it.isGaaSEvent() && it.isCanceledOrCompleted() }
            ?.run {
                log.info { "[RemoveCancelOrCompletedGaaSEvent] {\"message\":\"Remove cancel or completed event, event id: $id\"}" }
                gaasEventIds.remove(id)
            }
    }
}

/*
* When the event starts, it is checked whether it is a GaaS study group, and if so,
* a task is started to periodically check the list of participants and record their attendance.
* */
fun ScheduledEvent.recordEventParticipation() {
    val filePath = createDataFile()

    val currentDate = LocalDate.now()
    val start = currentDate.atTime(21, 0, 0)
    val end = currentDate.atTime(22, 0, 0)
    val participantCount = mutableListOf<Int>()

    timer.schedule(object : TimerTask() {
        override fun run() {
            val channel = channel!!
            channel.asVoiceChannel().run {
                participantCount.add(members.size)
                val newContent = members.map { "${it.nickname ?: it.user.name} : ${it.id}" }
                writeParticipantsIntoFile(newContent, filePath)
            }
        }
    }, start.toDate(), 3.minutes.inWholeMilliseconds)

    timer.schedule(
        object : TimerTask() {
            override fun run() {
                writeStaticsSummaryIntoFile(participantCount, filePath)
                timer.cancel()
                log.info { "[RecordTaskFinished] {\"message\":\"Recording task has been finished.\"}" }
            }
        }, end.toDate()
    )
}

private fun ScheduledEvent.isGaaSEvent() = id in gaasEventIds

private fun ScheduledEvent.isCanceledOrCompleted() =
    status == ScheduledEvent.Status.CANCELED || status == ScheduledEvent.Status.COMPLETED

@Synchronized
private fun writeStaticsSummaryIntoFile(participantCount: List<Int>, filePath: Path) {
    val avgStatics = "Avg: ${participantCount.average().roundToInt()}"
    val maxStatics = "Max: ${participantCount.max()}"
    writeString(filePath, avgStatics + lineSeparator(), APPEND)
    writeString(filePath, maxStatics + lineSeparator(), APPEND)
}

@Synchronized
private fun writeParticipantsIntoFile(newContent: List<String>, filePath: Path) {
    val currentTime = dateFormatter.format(LocalDateTime.now())
    buildString {
        append(currentTime)
        append(lineSeparator())
        newContent.forEach { append("$it${lineSeparator()}") }
        append(lineSeparator())
    }.also { result -> writeString(filePath, result, APPEND) }
}

private fun createDataFile(): Path {
    val fileName = "${DATABASE_FILENAME_PREFIX}${LocalDate.now()}.db"
    File(DATABASE_DIRECTORY).createDirectoryIfNotExists()
    return File(DATABASE_DIRECTORY + fileName).createFileIfNotExists()
}

private fun LocalDateTime.toDate() = Date.from(atZone(ZoneId.systemDefault()).toInstant())
