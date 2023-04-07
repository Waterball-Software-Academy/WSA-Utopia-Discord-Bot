package tw.waterballsa.utopia.commons.extensions

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDateTime.toDate() = Date.from(atZone(ZoneId.systemDefault()).toInstant())
