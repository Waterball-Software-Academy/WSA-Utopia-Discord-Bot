package tw.waterballsa.utopia.mongo.gateway

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test

internal class CriteriaTest {

    @Test
    fun emptyCriteria() {
        assertThat(Criteria().getCriteriaObject())
                .isEqualTo(Document())
    }

    @Test
    fun nameIsTom() {
        assertThat(Criteria("name").`is`("tom").getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to "tom")))
    }

    @Test
    fun nameIsNull() {
        assertThat(Criteria("name").isNull().getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to null)))
    }

    @Test
    fun nameNotEqualToTom() {
        assertThat(Criteria("name").ne("tom").getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to Document(mapOf("\$ne" to "tom")))))
    }

    @Test
    fun nameIsTomAndAgeNotEqualTo18() {
        assertThat(Criteria("name").`is`("tom")
                .and("age").ne(18)
                .getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to "tom", "age" to Document(mapOf("\$ne" to 18)))))
    }
}
