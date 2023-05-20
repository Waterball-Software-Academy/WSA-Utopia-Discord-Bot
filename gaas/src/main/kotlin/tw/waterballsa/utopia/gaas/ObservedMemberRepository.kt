package tw.waterballsa.utopia.gaas

import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

@Component
class ObservedMemberRepository {

    companion object {
        private const val DATABASE_FILE_NANE = "member-list.db"
        private const val DATABASE_FILE_PATH = "data/gaas/observed"
    }

    private val observedMemberPath: Path = Path(DATABASE_FILE_PATH)
        .createDirectories()
        .resolve(DATABASE_FILE_NANE)
        .takeIf { it.notExists() }
        ?.createFile()
        ?: Path(DATABASE_FILE_PATH, DATABASE_FILE_NANE)

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
                        options = arrayOf(StandardOpenOption.APPEND)
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
            observedMemberPath.writeLines(idToRecord.values.map { it.toString() })
        }
    }
}
