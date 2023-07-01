package tw.waterballsa.utopia.mongo.gatweay.config

import com.mongodb.client.MongoClients
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.testcontainers.containers.MongoDBContainer


private const val TEST_DATABASE = "test_database"

open class TestMongoConfiguration : AbstractMongoClientConfiguration(), AfterAllCallback {

    companion object {
        private lateinit var mongoDBContainer: MongoDBContainer

        init {
            startMongoDBContainer()
        }

        private fun startMongoDBContainer() {
            if (!::mongoDBContainer.isInitialized) {
                mongoDBContainer = MongoDBContainer("mongo:6.0.6")
                        .withReuse(true)
                mongoDBContainer.start()
            }
        }

        fun mongoTemplate(): MongoTemplate {
            return MongoTemplate(MongoClients.create(mongoDBContainer.connectionString), TEST_DATABASE)
        }
    }

    override fun getDatabaseName(): String {
        return TEST_DATABASE
    }

    override fun afterAll(p0: ExtensionContext?) {
        if (!mongoDBContainer.isCreated) {
            mongoDBContainer.stop()
        }
    }
}

