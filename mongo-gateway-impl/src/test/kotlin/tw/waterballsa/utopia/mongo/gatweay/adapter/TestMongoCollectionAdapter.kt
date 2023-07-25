package tw.waterballsa.utopia.mongo.gatweay.adapter

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.MongoTemplate
import tw.waterballsa.utopia.mongo.gateway.Criteria
import tw.waterballsa.utopia.mongo.gateway.Query
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import tw.waterballsa.utopia.mongo.gatweay.config.TestMongoBase
import tw.waterballsa.utopia.mongo.gatweay.config.TestMongoConfiguration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME


private const val TEST_COLLECTION = "test_collection"

class TestMongoCollectionAdapter : TestMongoBase() {

    private lateinit var mongoCollectionAdapter: MongoCollectionAdapter<TestDocument, String>
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    internal fun setUp() {
        mongoTemplate = TestMongoConfiguration.mongoTemplate()
        val documentInformation = MappingMongoDocumentInformation(
            TEST_COLLECTION,
            TestDocument::class.java,
            String::class.java,
            "id"
        )
        mongoCollectionAdapter = MongoCollectionAdapter(
            mongoTemplate,
            documentInformation
        )
    }

    override fun collectionName(): String = TEST_COLLECTION

    @Nested
    inner class Save {

        @Test
        @DisplayName("When the `@Id` field is null, the value should return the auto-generated ObjectId after insertion.")
        fun insertOne() {
            assertThat(mongoCollectionAdapter.save(TestDocument(age = 18, name = "test-user")))
                .extracting(TestDocument::id)
                .isNotNull
        }

        @Test
        fun update() {
            val document = TestDocument(id = "168", age = 25, name = "old-user").saveTestDocument()
            document.name = "new-user"

            val newDocument = mongoCollectionAdapter.save(document)

            newDocument.shouldBeInTheDB()
        }

        private fun TestDocument.shouldBeInTheDB() {
            val expectedDocument =
                mongoTemplate.findOne(query(where("_id").`is`(id)), TestDocument::class.java, TEST_COLLECTION)!!
            assertThat(id).isEqualTo(expectedDocument.id)
            assertThat(age).isEqualTo(expectedDocument.age)
            assertThat(name).isEqualTo(expectedDocument.name)
            assertThat(createdDate).isEqualTo(expectedDocument.createdDate)
        }
    }

    @Nested
    inner class FindOne {

        @Test
        fun idMatch() {
            with(TestDocument(id = "168")) {
                saveTestDocument()
                assertThat(mongoCollectionAdapter.findOne(id!!)).isEqualTo(this)
            }
        }

        @Test
        fun idMismatch() {
            assertThat(mongoCollectionAdapter.findOne("168")).isNull()
        }
    }

    @Nested
    inner class FindAll {

        @Test
        fun empty() {
            assertThat(mongoCollectionAdapter.findAll()).isEmpty()
        }

        @Test
        fun twoDocuments() {
            val documents = listOf("123", "456")
                .map { TestDocument(id = it).saveTestDocument() }

            assertThat(mongoCollectionAdapter.findAll()).isEqualTo(documents)
        }
    }

    @Nested
    inner class FindByQuery {

        @Test
        fun idIs123AndNameIsTom() {
            with(TestDocument(id = "123", name = "tom")) {
                saveTestDocument()
                assertThat(
                    mongoCollectionAdapter.find(
                        Query(
                            Criteria("_id").`is`("123").and("name").`is`("tom")
                        )
                    )
                ).containsExactlyInAnyOrder(this)
            }
        }
    }

    @Nested
    inner class Remove {

        @Test
        fun notExist() {
            assertThat(mongoCollectionAdapter.remove(TestDocument(id = "123"))).isFalse
        }

        @Test
        fun exist() {
            with(TestDocument(id = "123")) {
                saveTestDocument()
                assertThat(mongoCollectionAdapter.remove(this)).isTrue
            }
        }
    }

    @Nested
    inner class RemoveAll {

        @Test
        fun removeAll() {
            val documents = listOf("123", "456", "78")
                .map { TestDocument(id = it).saveTestDocument() }

            assertThat(mongoCollectionAdapter.removeAll(documents)).isEqualTo(documents.size.toLong())
        }

    }

    private fun createTestDocument(id: String? = null, age: Int? = null, name: String? = null) {
        mongoTemplate.insert(
            Document(
                mapOf(
                    Pair("_id", id),
                    Pair("age", age),
                    Pair("name", name)
                )
            ), TEST_COLLECTION
        )
    }

    private fun TestDocument.saveTestDocument(): TestDocument =
        mongoTemplate.save(toDocument(), TEST_COLLECTION).toDocument()
}

data class TestDocument(
    val id: String? = null,
    var age: Int? = null,
    var name: String? = null,
    val createdDate: LocalDateTime? = LocalDateTime.now()
) {

    constructor(id: String?, age: Int?, name: String?, createdDate: String?) :
            this(id, age, name, createdDate?.toLocalDateTime())

    fun toDocument(): Document {
        return Document()
            .append("_id", id)
            .append("age", age)
            .append("name", name)
            .append("createdDate", createdDate.toString())
    }
}

private fun Document.toDocument(): TestDocument =
    TestDocument(getString("_id"), getInteger("age"), getString("name"), getString("createdDate"))

private fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, ISO_LOCAL_DATE_TIME)

