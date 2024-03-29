package tw.waterballsa.utopia.mongo.gateway

import org.bson.Document

class Criteria() : CriteriaDefinition {

    companion object {
        private val NOT_SET = Object()
    }

    private var criteriaChain = ArrayList<Criteria>()

    private val criteria = LinkedHashMap<String, Any?>()

    private var key: String? = null

    private var isValue: Any? = NOT_SET

    constructor(key: String) : this() {
        this.criteriaChain.add(this)
        this.key = key
    }

    constructor(criteriaChain: ArrayList<Criteria>, key: String) : this() {
        this.criteriaChain = criteriaChain
        this.criteriaChain.add(this)
        this.key = key
    }

    fun and(key: String): Criteria {
        return Criteria(criteriaChain, key)
    }

    fun `is`(value: Any?): Criteria {
        this.isValue = value
        return this
    }

    fun isNull(): Criteria {
        return `is`(null)
    }

    fun ne(value: Any?): Criteria {
        criteria["\$ne"] = value
        return this
    }

    fun gt(value: Int): Criteria {
        criteria["\$gt"] = value
        return this
    }

    fun gte(value: Int): Criteria {
        criteria["\$gte"] = value
        return this
    }

    fun lt(value: Int): Criteria {
        criteria["\$lt"] = value
        return this
    }

    fun lte(value: Int): Criteria {
        criteria["\$lte"] = value
        return this
    }

    fun `in`(values: Collection<*>): Criteria {
        criteria["\$in"] = values
        return this
    }

    fun `in`(vararg values: Any): Criteria {
        return `in`(values.asList())
    }

    fun nin(values: Collection<*>): Criteria {
        criteria["\$nin"] = values
        return this
    }

    fun nin(vararg values: Any): Criteria {
        return nin(values.asList())
    }

    fun exists(value: Boolean): Criteria {
        criteria["\$exists"] = value
        return this
    }

    fun orOperator(vararg values: Criteria): Criteria {
        return orOperator(values.asList())
    }

    fun orOperator(criteria: Collection<Criteria>): Criteria {
        return registerCriteriaChainElement(Criteria("\$or").`is`(createCriteriaList(criteria)))
    }

    private fun createCriteriaList(criteriaList: Collection<Criteria>): List<Document> {
        return criteriaList.map { it.getCriteriaObject() }
    }

    private fun registerCriteriaChainElement(criteria: Criteria): Criteria {
        criteriaChain.add(criteria)
        return this
    }

    override fun getCriteriaObject(): Document {
        return if (criteriaChain.size == 1) {
            criteriaChain[0].getSingleCriteriaObject()
        } else if (criteriaChain.isEmpty() && criteria.isNotEmpty()) {
            getSingleCriteriaObject()
        } else {
            val criteriaObject = Document()
            criteriaChain.forEach {
                val document: Document = it.getSingleCriteriaObject()
                for (k in document.keys) {
                    criteriaObject[k] = document[k]
                }
            }
            criteriaObject
        }
    }

    private fun getSingleCriteriaObject(): Document {
        val document = Document()
        criteria.entries.forEach {
            document[it.key] = it.value
        }
        val queryCriteria = Document()
        if (NOT_SET != (isValue)) {
            queryCriteria[key] = isValue
            queryCriteria.putAll(document)
        } else {
            queryCriteria[key] = document
        }
        return queryCriteria
    }
}
