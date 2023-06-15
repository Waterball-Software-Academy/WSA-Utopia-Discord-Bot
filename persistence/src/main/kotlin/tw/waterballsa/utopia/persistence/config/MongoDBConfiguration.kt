package tw.waterballsa.utopia.persistence.config

import ch.qos.logback.core.util.OptionHelper
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
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
        val factory = SimpleMongoClientDatabaseFactory(MongoClients.create(settings), wsaDiscordProperties.mongoDatabase)

        // remove _class field
        val converter = MappingMongoConverter(DefaultDbRefResolver(factory), MongoMappingContext())
        converter.typeMapper = DefaultMongoTypeMapper(null)

        mongoTemplate = MongoTemplate(factory, converter)
    }
}
