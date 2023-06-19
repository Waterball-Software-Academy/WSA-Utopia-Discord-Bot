package tw.waterballsa.utopia.gaas

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.extensions.createFileWithFileName
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.writeLines

@Component
class ObservedMemberRepository {

    companion object {
        private const val DATABASE_FILE_NANE = "member-list.db"
        private const val DATABASE_FILE_PATH = "data/gaas/observed"
    }

    private val observedMemberPath: Path = Path(DATABASE_FILE_PATH).createFileWithFileName(DATABASE_FILE_NANE)

    private val idToRecord = mutableMapOf<String, ObservedMemberRecord>()

    init {
        observedMemberPath
            .readLines()
            .filterNot { it.isBlank() }
            .map { ObservedMemberRecord.createFromRecord(it) }
            .associateByTo(idToRecord) { it.id }
    }

    internal fun addObservedMember(observedMemberRecord: ObservedMemberRecord): ObservedMemberRecord =
        synchronized(idToRecord) {
            idToRecord.computeIfAbsent(observedMemberRecord.id) { observedMemberRecord }
                .also {
                    observedMemberPath.writeLines(
                        lines = listOf(it.toString()),
                        options = arrayOf(APPEND)
                    )
                }
        }

    internal fun exists(id: String): Boolean = synchronized(idToRecord) { idToRecord.containsKey(id) }

    internal fun removeObservedMember(id: String) {
        removeObservedMemberByIds(listOf(id))
    }

    internal fun findAll(): List<ObservedMemberRecord> = synchronized(idToRecord) { idToRecord.values.toList() }

    internal fun removeObservedMemberByIds(ids: Collection<String>) {
        synchronized(idToRecord) {
            idToRecord.keys.removeAll(ids.toSet())
            observedMemberPath.writeLines(idToRecord.values.map(ObservedMemberRecord::toString))
        }
    }
}
