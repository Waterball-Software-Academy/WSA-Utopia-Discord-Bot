package domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CommandInfoTest {

    @Test
    fun empty_option() {
        val commandInfo = CommandInfo("ping", "ping-pong", "ping ping pong pong")

        val actual = commandInfo.toTableRow()

        assertThat(actual.columns).containsExactlyInAnyOrder("ping-pong", "", "ping ping pong pong")
    }

    @Test
    fun one_option() {
        val commandInfo = CommandInfo("ping", "ping-pong", "ping ping pong pong",
                listOf(CommandOption("option-1", "INTEGER", "a number")))

        val actual = commandInfo.toTableRow()

        assertThat(actual.columns).containsExactlyInAnyOrder("ping-pong", "option-1(INTEGER): a number", "ping ping pong pong")
    }

    @Test
    fun multi_options() {
        val commandInfo = CommandInfo("ping", "ping-pong", "ping ping pong pong",
                listOf(CommandOption("option-1", "INTEGER", "a number"),
                        CommandOption("option-2", "CHAR", "a char")))

        val actual = commandInfo.toTableRow()

        assertThat(actual.columns).containsExactlyInAnyOrder("ping-pong", "option-1(INTEGER): a number<br>option-2(CHAR): a char",
                "ping ping pong pong")
    }
}
