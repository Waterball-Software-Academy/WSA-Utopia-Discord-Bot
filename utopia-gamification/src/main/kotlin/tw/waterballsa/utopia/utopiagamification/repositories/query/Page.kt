package tw.waterballsa.utopia.utopiagamification.repositories.query

interface Page<T>  {

    fun getNumber(): Int

    fun getSize(): Int

    fun getNumberOfElements(): Int

    fun getContent(): List<T>

    fun hasContent(): Boolean

    fun isFirst(): Boolean

    fun isLast(): Boolean

    fun hasNext(): Boolean

    fun hasPrevious(): Boolean

    fun getPageable(): Pageable = PageRequest.of(getNumber(), getSize())

    fun nextPageable(): Pageable

    fun previousPageable(): Pageable

    fun <R> map(converter: (T) -> R): Page<R>

    fun nextOrLastPageable(): Pageable = if(hasNext()) nextPageable() else getPageable()

    fun previousOrFirstPageable(): Pageable = if(hasPrevious()) previousPageable() else getPageable()

    fun getTotalPages(): Int

    fun getTotalElements(): Long
}
