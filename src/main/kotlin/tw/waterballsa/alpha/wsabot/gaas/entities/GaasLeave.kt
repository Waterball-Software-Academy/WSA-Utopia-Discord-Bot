package tw.waterballsa.alpha.wsabot.gaas.entities

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GaasLeave(
    val id: String,
    val reason: String,
    val createTime: String = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now())
) {
}