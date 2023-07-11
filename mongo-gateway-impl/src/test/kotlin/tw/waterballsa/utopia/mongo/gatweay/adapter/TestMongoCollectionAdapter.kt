package tw.waterballsa.utopia.mongo.gatweay.adapter

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import tw.waterballsa.utopia.mongo.gatweay.config.TestMongoBase
import tw.waterballsa.utopia.mongo.gatweay.config.TestMongoConfiguration


private const val TEST_COLLECTION = "test_collection"

class TestMongoCollectionAdapter : TestMongoBase() {

    private lateinit var mongoCollectionAdapter: MongoCollectionAdapter<TestDocument, String>
    private lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    internal fun setUp() {
        mongoTemplate = TestMongoConfiguration.mongoTemplate()
        mongoCollectionAdapter = MongoCollectionAdapter(
                mongoTemplate,
                MappingMongoDocumentInformation(TEST_COLLECTION,
                        TestDocument::class.java,
                        String::class.java,
                        "id")
        )
    }

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
            createTestDocument(id = "168", age = 25, name = "old-user")

            mongoCollectionAdapter.save(TestDocument(id = "168", age = 18, name = "new-user"))

            documentInDBShouldBe("168", 18, "new-user")
        }

        private fun documentInDBShouldBe(id: String, age: Int, name: String) {
            assertThat(mongoTemplate.findOne(Query(Criteria.where("_id").`is`(id)), Document::class.java, TEST_COLLECTION))
                    .isEqualTo(Document(mapOf(
                            Pair("_id", id),
                            Pair("age", age),
                            Pair("name", name)
                    )))
        }
    }

    @Nested
    inner class FindOne {

        @Test
        fun idMatch() {
            createTestDocument(id = "168")

            assertThat(mongoCollectionAdapter.findOne("168"))
                    .extracting("id")
                    .isEqualTo("168")
        }

        @Test
        fun idMismatch() {
            assertThat(mongoCollectionAdapter.findOne("168"))
                    .isNull()
        }
    }

    @Nested
    inner class FindAll {

        @Test
        fun empty() {
            assertThat(mongoCollectionAdapter.findAll())
                    .isEmpty()
        }

        @Test
        fun twoDocuments() {
            createTestDocument(id = "123")
            createTestDocument(id = "456")

            assertThat(mongoCollectionAdapter.findAll())
                    .extracting("id")
                    .containsExactlyInAnyOrder("123", "456")
        }
    }

    @Nested
    inner class Remove {

        @Test
        fun notExist() {
            assertThat(mongoCollectionAdapter.remove(TestDocument(id = "123")))
                    .isFalse
        }

        @Test
        fun exist() {
            createTestDocument(id = "123")

            assertThat(mongoCollectionAdapter.remove(TestDocument(id = "123")))
                    .isTrue
        }
    }

    @Test
    fun removeAll() {
        createTestDocument(id = "123")
        createTestDocument(id = "456")
        createTestDocument(id = "78")

        assertThat(mongoCollectionAdapter.removeAll(listOf(TestDocument(id = "123"), TestDocument(id = "456"))))
                .isEqualTo(2L)
    }

    private fun createTestDocument(id: String? = null, age: Int? = null, name: String? = null) {
        mongoTemplate.insert(Document(mapOf(
                Pair("_id", id),
                Pair("age", age),
                Pair("name", name)
        )), TEST_COLLECTION)
    }

    override fun collectionName(): String = TEST_COLLECTION
}


data class TestDocument(val id: String? = null, val age: Int? = null, val name: String? = null)
