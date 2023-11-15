package tw.waterballsa.utopia.utopiagamification.repositories.query

import tw.waterballsa.utopia.utopiagamification.repositories.query.Pageable.Companion.unpaged
import kotlin.math.ceil
import kotlin.reflect.safeCast

class PageImpl<T> constructor(
    private val content: List<T>,
    private val pageable: Pageable,
    private var total: Long
): Page<T> {

    constructor(content: List<T>?) : this(content?: emptyList(), unpaged(), content?.size?.toLong()?: 0)

    init {
        if (content.isNotEmpty()) {
            val offset = pageable.getOffset()
            if(offset + pageable.getPageSize() > total) {
                total = offset + content.size
            }
        }
    }

    override fun getNumberOfElements(): Int = content.size

    override fun isFirst(): Boolean = !hasPrevious()

    override fun isLast(): Boolean = !hasNext()

    override fun nextPageable(): Pageable = if(hasNext())  pageable.next() else unpaged()

    override fun previousPageable(): Pageable = if(hasPrevious())  pageable.previousOrFirst() else unpaged()

    override fun hasPrevious(): Boolean = getNumber() > 0

    override fun hasNext(): Boolean = getNumber() + 1 < getTotalPages()

    override fun getNumber(): Int = if(pageable.isPaged())  pageable.getPageNumber() else 0

    override fun getTotalPages(): Int = if(getSize() == 0) 1 else  ceil(total.toDouble() / getSize().toDouble()).toInt()

    override fun getSize(): Int = if(pageable.isPaged())  pageable.getPageSize() else content.size

    override fun hasContent(): Boolean = content.isNotEmpty()

    override fun getContent(): List<T> = content

    override fun getTotalElements(): Long = total

    override fun <R> map(converter: (T) -> R): Page<R> = PageImpl(content.map { converter.invoke(it) }, getPageable(), total)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other?.javaClass != this.javaClass) {
            return false
        }
        val that = Page::class.safeCast(other) ?: return false
        val contentEqual = this.content == that.getContent()
        val pageableEqual = this.pageable == that.getPageable()
        return contentEqual && pageableEqual
    }

    override fun hashCode(): Int {
        var result = 17
        result += 31 * pageable.hashCode()
        result += 31 * content.hashCode()
        return result
    }

    override fun toString(): String {
        val contentType = "UNKNOWN"
        return "Page ${getNumber() + 1} of ${getTotalPages()} containing $contentType instances"
    }
}
