package tw.waterballsa.utopia.commons.utils

import java.io.File
import java.nio.file.Path

fun File.createDirectoryIfNotExists(): Path {
    if (!exists()) {
        if (!mkdirs()) {
            throw java.lang.IllegalStateException("Cannot create the directory '$name'.")
        }
    }
    return toPath()
}

fun File.createFileIfNotExists(): Path {
    if (!exists()) {
        if (!createNewFile()) {
            throw java.lang.IllegalStateException("Cannot create the file '$name'.")
        }
    }
    return toPath()
}
