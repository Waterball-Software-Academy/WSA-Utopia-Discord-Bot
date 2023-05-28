package tw.waterballsa.utopia.commons.extensions

import mu.KotlinLogging
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.hours

private val log = KotlinLogging.logger {}


fun Timer.onStart(task: TimerTask, startTime: Date, period: Long) = schedule(task, startTime, period)

fun Timer.onEnd(task: TimerTask, endTime: Date) {
    schedule(timerTask {
        task.run()
        this@onEnd.cancel()
        log.info { "[TimerTaskFinished] {\"message\":\"Timer task has been closed.\"}" }
    }, endTime)
}

fun Timer.dailyScheduling(hourOfDay: Int, minutes: Int, seconds: Int, task: Runnable) {
    dailyScheduling(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, seconds)
    }, task)
}

fun Timer.dailyScheduling(dailyTimeAsCalendar: Calendar, task: Runnable) {
    val time = dailyTimeAsCalendar.time
    scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            task.run()
        }
    }, time, 24.hours.inWholeMilliseconds) // 24 hours in milliseconds
}

fun Timer.scheduleDelay(delay: Long, task: Runnable) {
    schedule(object : TimerTask() {
        override fun run() {
            task.run()
        }
    }, delay)
}

