package tw.waterballsa.utopia.commons.mongo

import ch.qos.logback.core.util.OptionHelper
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.springframework.data.mongodb.core.MongoTemplate
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties


open class MongoDBConfiguration(wsaDiscordProperties: WsaDiscordProperties) {

    val mongoTemplate: MongoTemplate

    init {
        val uri = OptionHelper.getEnv("MONGO_CONNECTION_URI")?.trim()
                ?: "mongodb://localhost:28017"
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(uri))
                .applyToConnectionPoolSettings { builder ->
                    builder.maxSize(10)
                }
                .build()
        mongoTemplate = MongoTemplate(MongoClients.create(settings), wsaDiscordProperties.mongoDatabase)
    }
}
