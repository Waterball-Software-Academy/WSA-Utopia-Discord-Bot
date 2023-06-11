package tw.waterballsa.utopia.gaas.extensions

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists


internal fun Path.createFileIfNotExists(): Path =
    when (exists()) {
        true -> this
        false -> createFile()
    }

internal fun Path.createFileWithFileName(fileName: String): Path =
    createDirectories()
        .resolve(fileName)
        .createFileIfNotExists()
