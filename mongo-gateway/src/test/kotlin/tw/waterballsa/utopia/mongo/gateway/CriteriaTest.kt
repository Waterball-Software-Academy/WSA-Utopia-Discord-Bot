package tw.waterballsa.utopia.mongo.gateway

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CriteriaTest {

    @Test
    fun emptyCriteria() {
        assertThat(Criteria().getCriteriaObject())
                .isEqualTo(Document())
    }

    @Test
    fun nameIsTom() {
        val expectedValue =
                """
                { "name":"Tom" }
                """
        assertThatJson(Criteria("name").`is`("Tom").getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameIsNull() {
        val expectedValue =
                """
                { "name":null }
                """
        assertThatJson(Criteria("name").isNull().getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameNotEqualToTom() {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}ne": "Tom"
                  }
                }
                """
        assertThatJson(Criteria("name").ne("Tom").getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameIsTomAndAgeNotEqualTo18() {
        val expectedValue =
                """
                {
                  "name": "Tom",
                  "age": {
                    "${'$'}ne": 18
                  }
                }
                """
        assertThat(Criteria("name").`is`("Tom")
                .and("age").ne(18).getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to "Tom", "age" to Document(mapOf("\$ne" to 18)))))
    }
}
