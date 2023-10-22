package tw.waterballsa.utopia.mongo.gatweay.config

import ch.qos.logback.core.util.OptionHelper.getEnv
import ch.qos.logback.core.util.OptionHelper.getSystemProperty
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.mongo.gatweay.adapter.MappingMongoDocumentInformation
import tw.waterballsa.utopia.mongo.gatweay.adapter.MongoCollectionAdapter
import kotlin.reflect.full.findAnnotation

@Configuration
open class MongoDBConfiguration {
    companion object {
        private const val MONGO_CONNECTION_URI_KEY = "MONGO_CONNECTION_URI"

        internal val MAPPER = ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Bean
    open fun mongoTemplate(context: ApplicationContext): MongoTemplate {
        val uri = getEnv(MONGO_CONNECTION_URI_KEY)
                ?: getSystemProperty(MONGO_CONNECTION_URI_KEY, "mongodb://localhost:28017")
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(uri.trim()))
                .applyToConnectionPoolSettings { builder ->
                    builder.maxSize(10)
                }
                .build()
        val wsaDiscordProperties = context.getBean(WsaDiscordProperties::class.java)
        val factory =
                SimpleMongoClientDatabaseFactory(MongoClients.create(settings), wsaDiscordProperties.mongoDatabase)

        // remove _class field
        val converter = MappingMongoConverter(DefaultDbRefResolver(factory), MongoMappingContext())
        converter.typeMapper = DefaultMongoTypeMapper(null)

        return MongoTemplate(factory, converter)
    }
}

@Component
open class MyBeanFactoryPostProcessor : BeanFactoryPostProcessor, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val packageName = "tw.waterballsa.utopia"

        val annotatedClasses = Reflections(packageName).getTypesAnnotatedWith(Document::class.java)

        for (annotatedClass in annotatedClasses) {
            val idField = annotatedClass.declaredFields
                    .find { it.isAnnotationPresent(Id::class.java) }!!
            val resolvableType: ResolvableType =
                    ResolvableType.forClassWithGenerics(MongoCollection::class.java, annotatedClass, idField.type)
            val beanDefinition = RootBeanDefinition()
            beanDefinition.setTargetType(resolvableType)
            beanDefinition.autowireMode = AbstractBeanDefinition.AUTOWIRE_BY_TYPE
            beanDefinition.isAutowireCandidate = true

            val bf: DefaultListableBeanFactory = beanFactory as DefaultListableBeanFactory
            val collectionName = annotatedClass.kotlin.findAnnotation<Document>()?.collection
                    ?.ifEmpty { annotatedClass.simpleName }!!

            val mongoTemplate = applicationContext.getBean(MongoTemplate::class.java)
            val documentInformation = MappingMongoDocumentInformation(collectionName, annotatedClass, idField.type, idField.name)
            val mongoCollection = MongoCollectionAdapter(mongoTemplate, documentInformation)

            val beanName = "${annotatedClass.simpleName}MongoCollection"
            bf.registerBeanDefinition(beanName, beanDefinition)
            bf.registerSingleton(beanName, mongoCollection)
        }
    }

}
