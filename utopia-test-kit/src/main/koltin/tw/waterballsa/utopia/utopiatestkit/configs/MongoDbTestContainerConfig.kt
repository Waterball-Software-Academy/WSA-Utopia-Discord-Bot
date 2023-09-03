package tw.waterballsa.utopia.utopiatestkit.configs

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.testcontainers.containers.MongoDBContainer
import java.lang.System.setProperty

@Configuration
open class MongoDbTestContainerConfig : AbstractMongoClientConfiguration(), BeforeEachCallback, AfterAllCallback {

    companion object {
        private const val TEST_DATABASE = "TEST_DATABASE"
        private lateinit var MONGO_DB_CONTAINER: MongoDBContainer

        init {
            startMongoDBContainer()
        }

        private fun startMongoDBContainer() {
            if (!::MONGO_DB_CONTAINER.isInitialized) {
                MONGO_DB_CONTAINER = MongoDBContainer("mongo:6.0.6")
                        .withReuse(true)
                MONGO_DB_CONTAINER.start()
                setProperty("MONGO_CONNECTION_URI", MONGO_DB_CONTAINER.connectionString)
            }
        }
    }

    @Autowired
    private lateinit var mongo: MongoTemplate

    override fun getDatabaseName(): String = TEST_DATABASE

    override fun beforeEach(context: ExtensionContext?) {
        mongo.db.drop()
    }

    override fun afterAll(context: ExtensionContext?) {
        if (!MONGO_DB_CONTAINER.isCreated) {
            MONGO_DB_CONTAINER.stop()
        }
    }

}