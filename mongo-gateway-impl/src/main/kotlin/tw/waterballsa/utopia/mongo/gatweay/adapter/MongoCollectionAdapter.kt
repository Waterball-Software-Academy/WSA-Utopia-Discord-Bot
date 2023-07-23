package tw.waterballsa.utopia.mongo.gatweay.adapter

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import tw.waterballsa.utopia.mongo.gateway.MongoCollection


private const val MONGO_ID_FIELD_NAME = "_id"

class MongoCollectionAdapter<TDocument, ID>(
        private val mongoTemplate: MongoTemplate,
        private val documentInformation: MappingMongoDocumentInformation<TDocument, ID>
) : MongoCollection<TDocument, ID> {

    private var objectMapper: ObjectMapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun save(document: TDocument): TDocument {
        return mongoTemplate.save(document.toBsonDocument(), documentInformation.collectionName)
                .toDomainDocument()
    }

    override fun findOne(id: ID): TDocument? {
        return mongoTemplate.findOne(Query.query(Criteria.where(MONGO_ID_FIELD_NAME).`is`(id)),
                org.bson.Document::class.java, documentInformation.collectionName)?.toDomainDocument()
    }

    override fun findAll(): List<TDocument> {
        return mongoTemplate.findAll(org.bson.Document::class.java, documentInformation.collectionName)
                .map { it.toDomainDocument() }
                .toList()
    }

    override fun find(query: tw.waterballsa.utopia.mongo.gateway.Query): List<TDocument> {
        return mongoTemplate.getCollection(documentInformation.collectionName)
                .find(query.getCriteria().getCriteriaObject(), org.bson.Document::class.java)
                .map { it.toDomainDocument() }
                .toList()
    }

    override fun remove(document: TDocument): Boolean {
        return mongoTemplate.remove(document.toBsonDocument(), documentInformation.collectionName).deletedCount == 1L
    }

    override fun removeAll(documents: Collection<TDocument>): Long {
        val ids = documents.map { it.toBsonDocument().get(MONGO_ID_FIELD_NAME) }
                .toList()
        return mongoTemplate.remove(Query.query(Criteria.where(MONGO_ID_FIELD_NAME).`in`(ids)), documentInformation.collectionName)
                .deletedCount
    }

    private fun TDocument.toBsonDocument(): org.bson.Document {
        return objectMapper.convertValue(this, org.bson.Document::class.java)!!
                .convertIdField(documentInformation.idFieldName, MONGO_ID_FIELD_NAME)
    }

    private fun org.bson.Document.toDomainDocument(): TDocument {
        convertIdField(MONGO_ID_FIELD_NAME, documentInformation.idFieldName)
        return objectMapper.readValue(objectMapper.writeValueAsString(this), documentInformation.documentClassType)
    }

    private fun org.bson.Document.convertIdField(oldKey: String, newKey: String): org.bson.Document {
        containsKey(oldKey).let {
            val value = get(oldKey)?.let { if (it is ObjectId) it.toString() else it }
            append(newKey, value)
            remove(oldKey)
        }
        return this
    }
}

