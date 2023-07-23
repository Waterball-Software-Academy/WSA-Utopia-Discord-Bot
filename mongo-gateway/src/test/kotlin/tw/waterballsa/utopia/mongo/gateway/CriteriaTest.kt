package tw.waterballsa.utopia.mongo.gateway

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.bson.Document
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CriteriaTest {

    @Test
    fun emptyCriteria() {
        assertThatJson(Criteria().getCriteriaObject())
                .isEqualTo("{}")
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
        assertThatJson(Criteria("name").`is`("Tom")
                .and("age").ne(18).getCriteriaObject())
                .isEqualTo(Document(mapOf("name" to "Tom", "age" to Document(mapOf("\$ne" to 18)))))
    }

    @Test
    fun ageGreaterThan20() {
        val expectedValue =
                """
                {
                  "age": {
                    "${'$'}gt": 20
                  }
                }
                """
        assertThatJson(Criteria("age").gt(20).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun ageGreaterThanAndEqual20() {
        val expectedValue =
                """
                {
                  "age": {
                    "${'$'}gte": 20
                  }
                }
                """
        assertThatJson(Criteria("age").gte(20).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun ageLessThan50() {
        val expectedValue =
                """
                {
                  "age": {
                    "${'$'}lt": 50
                  }
                }
                """
        assertThatJson(Criteria("age").lt(50).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun ageLessThanAndEqual50() {
        val expectedValue =
                """
                {
                  "age": {
                    "${'$'}lte": 50
                  }
                }
                """
        assertThatJson(Criteria("age").lte(50).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameInTomAndMaryWithVararg() {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}in": ["Tom","Mary"]
                  }
                }
                """
        assertThatJson(Criteria("name").`in`("Tom", "Mary").getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameInTomAndMaryWithList() {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}in": ["Tom","Mary"]
                  }
                }
                """
        assertThatJson(Criteria("name").`in`(listOf("Tom", "Mary")).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameNotInTomAndMaryWithVararg() {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}nin": ["Tom","Mary"]
                  }
                }
                """
        assertThatJson(Criteria("name").nin("Tom", "Mary").getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameNotInTomAndMaryWithList() {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}nin": ["Tom","Mary"]
                  }
                }
                """
        assertThatJson(Criteria("name").nin(listOf("Tom", "Mary")).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @ParameterizedTest
    @CsvSource(value = ["true,true", "false,false"])
    fun nameExistOrNot(existValue: Boolean, expectedExistValue: Boolean) {
        val expectedValue =
                """
                {
                  "name": {
                    "${'$'}exists": $expectedExistValue
                  }
                }
                """
        assertThatJson(Criteria("name").exists(existValue).getCriteriaObject())
                .isEqualTo(expectedValue)
    }

    @Test
    fun nameIsTomOrNameIsMaryAndAgeGreaterThan18() {
        val expectedValue =
                """
                {
                  "${'$'}or": [
                    {
                      "name": "Tom"
                    },
                    {
                      "name": "Mary",
                      "age": {
                        "${'$'}gt": 18
                      }
                    }
                  ]
                }    
                """
        assertThatJson(Criteria().orOperator(
                Criteria("name").`is`("Tom"),
                Criteria("name").`is`("Mary").and("age").gt(18)).getCriteriaObject())
                .isEqualTo(expectedValue)
    }
}
