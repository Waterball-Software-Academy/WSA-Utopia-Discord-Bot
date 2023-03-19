package tw.waterballsa.utopia.commons.mongo

import ch.qos.logback.core.util.OptionHelper.getEnv
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import org.bson.types.ObjectId


class MongoTest {
    var username = getEnv("MONGO_USERNAME")
    var password = getEnv("MONGO_PASSWORD")
    var host = getEnv("MONGO_HOST")
    // local 開發可以自己先換掉這邊
    val uri = "mongodb+srv://$username:$password@${host}/?retryWrites=true&w=majority"
    val dbName = "testdb"
    val collectionName = "testCollection"

    fun save(note: String): String {
        MongoClients.create(uri).use { client ->
            val database = client.getDatabase(dbName)
            val collection = database.getCollection(collectionName)
            val id = ObjectId()
            collection.insertOne(
                Document()
                    .append("_id", id)
                    .append("note", note)
            )
            return id.toString()
        }
    }

    fun query(id: String): String {
        MongoClients.create(uri).use { client ->
            val database = client.getDatabase(dbName)
            val collection = database.getCollection(collectionName)
            return collection.find(eq("_id", ObjectId(id))).first()?.toString() ?: "Nothing"
        }
    }
}
