package domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CommandDocumentTest {

    @Test
    fun empty_commands() {
        val commandDocument = CommandDocument()

        val actual = commandDocument.buildCommandTableMarkdown()

        assertThat(actual)
                .isEqualTo("""
                # Commands Document
                
                
                """.trimIndent())
    }

    @Test
    fun one_command() {
        val commandDocument = CommandDocument(listOf(
                CommandInfo("ping", "ping-pong", "ping ping pong pong",
                        listOf(CommandOption("option-1", "INTEGER", "a number")))
        ))

        val actual = commandDocument.buildCommandTableMarkdown()

        assertThat(actual)
                .isEqualTo("""
                # Commands Document
  
                ## ping
                | Commands  | Arguments                   | Description         |
                |:---------:| --------------------------- | ------------------- |
                | ping-pong | option-1(INTEGER): a number | ping ping pong pong |
                """.trimIndent())
    }

    @Test
    fun two_same_parent_commands() {
        val commandDocument = CommandDocument(listOf(
                CommandInfo("ping", "ping", "ping pong"),
                CommandInfo("ping", "ping-pong", "ping ping pong pong",
                        listOf(CommandOption("option-1", "INTEGER", "a number"))),
        ))

        val actual = commandDocument.buildCommandTableMarkdown()

        assertThat(actual)
                .isEqualTo("""
                # Commands Document
  
                ## ping
                | Commands  | Arguments                   | Description         |
                |:---------:| --------------------------- | ------------------- |
                |   ping    |                             | ping pong           |
                | ping-pong | option-1(INTEGER): a number | ping ping pong pong |
                """.trimIndent())
    }

    @Test
    fun two_different_commands() {
        val commandDocument = CommandDocument(listOf(
                CommandInfo("ping", "ping-pong", "ping ping pong pong",
                        listOf(CommandOption("option-1", "INTEGER", "a number"))),
                CommandInfo("bomb", "bomb", "big bomb",
                        listOf(CommandOption("option-1", "INTEGER", "a number")))
        ))

        val actual = commandDocument.buildCommandTableMarkdown()

        assertThat(actual)
                .isEqualTo("""
                # Commands Document
                
                ## ping
                | Commands  | Arguments                   | Description         |
                |:---------:| --------------------------- | ------------------- |
                | ping-pong | option-1(INTEGER): a number | ping ping pong pong |
                
                ## bomb
                | Commands | Arguments                   | Description |
                |:--------:| --------------------------- | ----------- |
                |   bomb   | option-1(INTEGER): a number | big bomb    |
                """.trimIndent())
    }
}
