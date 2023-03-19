package tw.waterballsa.utopia.commons.utils

import java.util.*

fun loadProperties(classPath: String): Properties {
    val properties = Properties()
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(classPath)
    properties.load(inputStream)
    return properties
}
