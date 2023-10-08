package tw.waterballsa.utopia.mongo.gatweay.adapter

import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.mongo.gatweay.config.MongoDBConfiguration.Companion.MAPPER
import tw.waterballsa.utopia.mongo.gateway.Query as MGQuery

private const val MONGO_ID_FIELD_NAME = "_id"

class MongoCollectionAdapter<TDocument, ID>(
    private val mongoTemplate: MongoTemplate,
    private val documentInformation: MappingMongoDocumentInformation<TDocument, ID>
) : MongoCollection<TDocument, ID> {

    override fun save(document: TDocument): TDocument =
        mongoTemplate.save(document.toBsonDocument(), documentInformation.collectionName)
            .toDomainDocument()

    override fun findOne(id: ID): TDocument? =
        mongoTemplate.findOne(
            Query.query(Criteria.where(MONGO_ID_FIELD_NAME).`is`(id)),
            Document::class.java, documentInformation.collectionName
        )?.toDomainDocument()

    override fun findAll(): List<TDocument> =
        mongoTemplate.findAll(Document::class.java, documentInformation.collectionName)
            .map { it.toDomainDocument() }

    override fun find(query: MGQuery): List<TDocument> =
        mongoTemplate.getCollection(documentInformation.collectionName)
            .find(query.getCriteria().getCriteriaObject(), org.bson.Document::class.java)
            .map { it.toDomainDocument() }
            .toList()

    override fun remove(document: TDocument): Boolean =
        mongoTemplate.remove(document.toBsonDocument(), documentInformation.collectionName).deletedCount == 1L

    override fun removeAll(documents: Collection<TDocument>): Long =
        with(documents) {
            val ids = map { it.toBsonDocument()[MONGO_ID_FIELD_NAME] }
            mongoTemplate.remove(
                Query.query(Criteria.where(MONGO_ID_FIELD_NAME).`in`(ids)),
                documentInformation.collectionName
            ).deletedCount
        }

    override fun removeAll() {
        mongoTemplate.dropCollection(documentInformation.collectionName)
    }

    private fun TDocument.toBsonDocument(): Document = MAPPER.convertValue(this, Document::class.java)!!
        .convertIdField(documentInformation.idFieldName, MONGO_ID_FIELD_NAME)

    private fun Document.toDomainDocument(): TDocument {
        convertIdField(MONGO_ID_FIELD_NAME, documentInformation.idFieldName)
        return MAPPER.convertValue(this, documentInformation.documentClassType)
    }

    private fun Document.convertIdField(oldKey: String, newKey: String): Document {
        containsKey(oldKey).let {
            val value = get(oldKey)?.let { if (it is ObjectId) it.toString() else it }
            append(newKey, value)
            remove(oldKey)
        }
        return this
    }
}

