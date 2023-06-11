package tw.waterballsa.utopia.commons.extensions

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists


fun Path.createFileIfNotExists(): Path =
    when (exists()) {
        true -> this
        false -> createFile()
    }

fun Path.createFileWithFileName(fileName: String): Path =
    createDirectories()
        .resolve(fileName)
        .createFileIfNotExists()
