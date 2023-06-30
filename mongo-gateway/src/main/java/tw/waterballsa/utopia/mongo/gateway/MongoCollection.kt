package tw.waterballsa.utopia.mongo.gateway

interface MongoCollection<TDocument, ID> {

    fun save(document: TDocument): TDocument

    fun findOne(id: ID): TDocument?

    fun findAll(): List<TDocument>
}
