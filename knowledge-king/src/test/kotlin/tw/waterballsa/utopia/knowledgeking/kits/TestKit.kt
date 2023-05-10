package tw.waterballsa.utopia.knowledgeking.kits

import org.junit.jupiter.api.Assertions

class TestKit {
    companion object {
        fun expect(function: () -> List<Any>) =
                ResultsExpectation(function())
    }
}

class ResultsExpectation(private val resultIterator: ListIterator<Any>) {
    constructor(results: List<Any>) : this(results.listIterator())

    fun <T> thenRaise(type: Class<T>, nextResultFurtherAssertion: (T) -> Unit = {}): ResultsExpectation {
        if (resultIterator.hasNext()) {
            val nextResult = resultIterator.next()
            Assertions.assertEquals(type, nextResult.javaClass)
            nextResultFurtherAssertion(type.cast(nextResult))
            return this
        } else {
            throw AssertionError("Expect ${type.name} to be raised, but nothing happened.")
        }
    }

    fun thenRaiseNothing(reason: String = "") {
        if (resultIterator.hasNext()) {
            val explanation = if (reason == "") "" else "because ${reason},"
            throw AssertionError("Expect 'nothing' to be raised, $explanation but one result '${resultIterator.next()}' found.")
        }
    }
}
