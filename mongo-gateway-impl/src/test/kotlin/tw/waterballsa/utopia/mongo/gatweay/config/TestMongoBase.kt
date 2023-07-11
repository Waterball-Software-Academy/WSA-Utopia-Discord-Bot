package tw.waterballsa.utopia.mongo.gatweay.config

import org.junit.jupiter.api.AfterEach
import org.springframework.data.mongodb.core.query.Query

abstract class TestMongoBase {
    abstract fun collectionName(): String

    @AfterEach
    fun clearAll() {
        TestMongoConfiguration.mongoTemplate().remove(Query(), collectionName())
    }
}
