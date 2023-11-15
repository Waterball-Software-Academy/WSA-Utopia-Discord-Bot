package tw.waterballsa.utopia.utopiagamification.repositories.query

import tw.waterballsa.utopia.utopiagamification.repositories.query.Unpaged.INSTANCE
import kotlin.reflect.safeCast

interface Pageable {

    companion object {
        fun unpaged(): Pageable = INSTANCE
    }

    fun isPaged(): Boolean = true

    fun isUnpaged(): Boolean = !isPaged()

    fun getPageNumber(): Int

    fun getPageSize(): Int

    fun getOffset(): Long

    // TODO: implement sort
    // fun getSort(): Sort

    fun previousOrFirst(): Pageable

    fun first(): Pageable

    fun next(): Pageable

    fun withPage(pageNumber: Int): Pageable

    fun hasPrevious(): Boolean
}

abstract class AbstractPageRequest(private val page: Int, private val size: Int) : Pageable {

    init {
        if (page < 0) {
            throw IllegalArgumentException("Page index must not be less than zero!");
        }

        if (size < 1) {
            throw IllegalArgumentException("Page size must not be less than one!");
        }
    }

    override fun getPageSize(): Int = size

    override fun getPageNumber(): Int = page

    override fun getOffset(): Long = page.times(size).toLong()

    override fun hasPrevious(): Boolean = page > 0

    override fun previousOrFirst(): Pageable = if (hasPrevious()) previous() else first()

    abstract fun previous(): Pageable

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + page
        result = prime * result + size
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other?.javaClass != this.javaClass) {
            return false
        }
        val other = AbstractPageRequest::class.safeCast(other) ?: return false
        return this.page == other.page && this.size == other.size
    }

}

class PageRequest private constructor(page: Int, size: Int) : AbstractPageRequest(page, size) {

    companion object {
        fun of(page: Int, size: Int) = PageRequest(page, size)

        fun ofSize(size: Int) = PageRequest(0, size)
    }

    override fun next(): Pageable = PageRequest(getPageNumber() + 1, getPageSize())

    override fun previous(): Pageable =
        if (getPageNumber() == 0) this else PageRequest(getPageNumber() - 1, getPageSize())

    override fun first(): Pageable = PageRequest(0, getPageSize())

    override fun withPage(pageNumber: Int): Pageable = PageRequest(pageNumber, getPageSize())

    override fun toString(): String = "Page request [number: ${getPageNumber()}, size ${getPageSize()}]"
}

enum class Unpaged : Pageable {

    INSTANCE;

    override fun isPaged(): Boolean = false

    override fun previousOrFirst(): Pageable = this

    override fun next(): Pageable = this

    override fun hasPrevious(): Boolean = false

    override fun getPageSize(): Int = throw UnsupportedOperationException()

    override fun getPageNumber(): Int = throw UnsupportedOperationException()

    override fun getOffset(): Long = throw UnsupportedOperationException()

    override fun first(): Pageable = this

    override fun withPage(pageNumber: Int): Pageable =
        if (pageNumber == 0) this else throw UnsupportedOperationException()
}
