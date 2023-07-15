package tw.waterballsa.utopia.mongo.gateway

class Query(private val criteriaDefinition: CriteriaDefinition) {

    fun getCriteria(): CriteriaDefinition {
        return this.criteriaDefinition
    }
}
