package tw.waterballsa.utopia.mongo.gateway

interface MongoCollection<TDocument, ID> {

    fun save(document: TDocument): TDocument

    fun findOne(id: ID): TDocument?

    fun findAll(): List<TDocument>

    fun remove(document: TDocument): Boolean

    fun removeAll(documents: Collection<TDocument>): Long

    fun find(query: Query): List<TDocument>

    fun removeAll()
}
