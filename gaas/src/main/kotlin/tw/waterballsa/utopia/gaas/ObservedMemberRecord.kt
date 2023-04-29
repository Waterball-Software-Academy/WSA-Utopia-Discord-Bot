package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.Member
import java.time.LocalDate

data class ObservedMemberRecord(
    val id: String,
    val name: String,
    val createdTime: LocalDate = LocalDate.now()
) {
    companion object {
        fun createFromMember(member: Member): ObservedMemberRecord =
            ObservedMemberRecord(member.id, member.nickname ?: member.effectiveName)

        fun createFromRecord(memberRecord: String): ObservedMemberRecord {
            val record = memberRecord.split(":")
            val createdTime = LocalDate.parse(record.last())
            return ObservedMemberRecord(record.first(), record[1], createdTime)
        }
    }

    override fun toString(): String = "$id:$name:$createdTime"
}
