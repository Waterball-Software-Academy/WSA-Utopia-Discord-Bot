package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.utopiagamification.repositories.query.Pageable
import tw.waterballsa.utopia.utopiagamification.repositories.query.Page
import tw.waterballsa.utopia.utopiagamification.repositories.query.PageImpl

interface PageableRepository<T> {
    fun findAll(pageable: Pageable): Page<T>
}

fun <T> Collection<T>.page(pageable: Pageable): Page<T> =
    drop(pageable.getOffset().toInt())
        .take(pageable.getPageSize())
        .let { PageImpl(it.toList(), pageable, size.toLong()) }
