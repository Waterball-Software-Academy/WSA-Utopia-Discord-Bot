package tw.waterballsa.utopia.gaas

import java.time.LocalDate
import java.time.LocalDate.now
import java.time.temporal.ChronoUnit.DAYS
import kotlin.math.absoluteValue

data class ObservedMemberRecord(
    val id: String,
    val name: String,
    val createdTime: LocalDate = now()
) {
    companion object {
        fun createFromRecord(memberRecord: String): ObservedMemberRecord {
            val record = memberRecord.split(":")
            val createdTime = LocalDate.parse(record.last())
            return ObservedMemberRecord(record.first(), record[1], createdTime)
        }
    }

    internal fun isCreatedTimeOver30Days(): Boolean =
        DAYS.between(createdTime, now()).absoluteValue >= 30

    override fun toString(): String = "$id:$name:$createdTime"
}
