package tw.waterballsa.utopia.commons.extensions

import java.io.File
import java.nio.file.Path

fun File.createDirectoryIfNotExists(): Path {
    if (!exists()) {
        if (!mkdirs()) {
            throw IllegalStateException("Cannot create the directory '$name'.")
        }
    }
    return toPath()
}

fun File.createFileIfNotExists(): Path {
    if (!exists()) {
        if (!createNewFile()) {
            throw IllegalStateException("Cannot create the file '$name'.")
        }
    }
    return toPath()
}
