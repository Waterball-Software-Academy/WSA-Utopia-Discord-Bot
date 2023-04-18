package tw.waterballsa.utopia.commons.utils

fun <T> Collection<T>.getRandomElements(x: Int): Collection<T> = shuffled().take(x)
