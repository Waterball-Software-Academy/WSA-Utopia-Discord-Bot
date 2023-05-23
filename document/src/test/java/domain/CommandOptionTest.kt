package domain

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

internal class CommandOptionTest {

    @Test
    fun document_all_fields() {
        val commandInfo = CommandOption("option-name", "INTEGER", "a number")

        val actual = commandInfo.toDocument()

        assertThat(actual).isEqualTo("option-name(INTEGER): a number")
    }
}
