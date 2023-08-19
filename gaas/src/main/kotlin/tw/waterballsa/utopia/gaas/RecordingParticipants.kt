package tw.waterballsa.utopia.gaas

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.onEnd
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.UtopiaListener
import java.lang.System.lineSeparator
import java.nio.file.Files.writeString
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

/*
* RecordingParticipants is one of the features in GaaS.
* It is used to record the attendance status for GaaS events.
* When an event starts, a periodic task is initiated to keep track of the participants.
* */

private val gaasEventIds = hashSetOf<String>()
private const val DATABASE_DIRECTORY = "data/gaas/participation-stats"
private const val DATABASE_FILENAME_PREFIX = "study-circle-participants-record"
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val log = KotlinLogging.logger {}

@Component
class RecordingParticipants(private val properties: WsaDiscordProperties) : UtopiaListener() {
    private var recordingTask: TimerTask? = null
    override fun onScheduledEventCreate(event: ScheduledEventCreateEvent) {
        with(event) {
            val partyChannelId = properties.wsaPartyChannelId
            scheduledEvent
                .takeIf { it.name.contains("遊戲微服務") && it.channel?.id == partyChannelId }
                ?.run {
                    log.info { "[GaaSEventCreated] {\"message\":\"New GaaS event has been created and collected, event id: $id\"}" }
                    gaasEventIds.add(id)
                }
        }
    }

    override fun onScheduledEventUpdateStatus(event: ScheduledEventUpdateStatusEvent) {
        with(event) {
            when  {
                !scheduledEvent.isGaaSEvent() -> return
                scheduledEvent.status == ScheduledEvent.Status.ACTIVE -> {
                    log.info { "[RecordTaskStarted] {\"message\":\"Start recording task, event id: ${scheduledEvent.id}\"}" }
                    recordingTask = scheduledEvent.recordEventParticipationStats()
                }
                scheduledEvent.isCanceledOrCompleted() -> {
                    log.info { "[RemoveCancelOrCompletedGaaSEvent] {\"message\":\"Remove cancel or completed event, event id: ${scheduledEvent.id}\"}" }
                    gaasEventIds.remove(scheduledEvent.id)
                    recordingTask?.cancel()
                }
                else -> return
            }
        }
    }
}

private fun ScheduledEvent.isGaaSEvent() = id in gaasEventIds

private fun ScheduledEvent.isCanceledOrCompleted() =
    status == ScheduledEvent.Status.CANCELED || status == ScheduledEvent.Status.COMPLETED

/*
* When the event starts, it is checked whether it is a GaaS study group, and if so,
* a task is started to periodically check the list of participants and record their attendance.
* */
private fun ScheduledEvent.recordEventParticipationStats(): TimerTask {
    val filePath = createParticipantsStatsFile()
    val today = LocalDate.now()
    val startTime = today.atTime(21, 0, 0).toDate()
    val endTime = today.atTime(22, 0, 0).toDate()
    val participantCount = hashSetOf<Int>()
    val period = 3.minutes.inWholeMilliseconds
    val recordingTask = recordParticipantsStatsAsFile(participantCount, filePath)
    Timer().run {
        onStart(recordingTask, startTime, period)
        onEnd(calculateAvgAndMaxParticipants(participantCount, filePath), endTime)
    }
    return recordingTask
}

private fun ScheduledEvent.recordParticipantsStatsAsFile(
    participantCount: MutableCollection<Int>,
    filePath: Path
) = timerTask {
    channel!!.asVoiceChannel().run {
        participantCount.add(members.size)
        val newContent = members.map { "${it.nickname ?: it.user.name} : ${it.id}" }
        writeParticipantsIntoFile(newContent, filePath)
    }
}

private fun calculateAvgAndMaxParticipants(
    participantCount: Collection<Int>,
    filePath: Path
) = timerTask { writeStaticsSummaryIntoFile(participantCount, filePath) }

@Synchronized
private fun writeStaticsSummaryIntoFile(participantCount: Collection<Int>, filePath: Path) {
    val avgStatics = "Avg: ${participantCount.average().roundToInt()}"
    val maxStatics = "Max: ${participantCount.max()}"
    buildString {
        append("$avgStatics${lineSeparator()}")
        append("$maxStatics${lineSeparator()}")
    }.also { result -> writeString(filePath, result, APPEND) }
}

@Synchronized
private fun writeParticipantsIntoFile(newContent: Collection<String>, filePath: Path) {
    val currentTime = dateFormatter.format(LocalDateTime.now())
    buildString {
        append("$currentTime${lineSeparator()}")
        newContent.forEach { append("$it${lineSeparator()}") }
        append(lineSeparator())
    }.also { result -> writeString(filePath, result, APPEND) }
}

private fun createParticipantsStatsFile(): Path {
    val path = Path(DATABASE_DIRECTORY)
        .createDirectories()
        .resolve("$DATABASE_FILENAME_PREFIX-${LocalDate.now()}.db")
    return if (path.exists()) path else path.createFile()
}
