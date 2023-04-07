package tw.waterballsa.utopia.commons.extensions

import mu.KotlinLogging
import java.util.*
import kotlin.concurrent.timerTask

private val log = KotlinLogging.logger {}
fun Timer.onStart(task: TimerTask, startTime: Date, period: Long) = schedule(task, startTime, period)

fun Timer.onEnd(task: TimerTask, endTime: Date) {
    schedule(timerTask {
        task.run()
        this@onEnd.cancel()
        log.info { "[TimerTaskFinished] {\"message\":\"Timer task has been closed.\"}" }
    }, endTime)
}
