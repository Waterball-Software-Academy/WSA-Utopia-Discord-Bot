package tw.waterballsa.utopia.mongo.gateway

import org.bson.Document

interface CriteriaDefinition {

    fun getCriteriaObject(): Document
}
