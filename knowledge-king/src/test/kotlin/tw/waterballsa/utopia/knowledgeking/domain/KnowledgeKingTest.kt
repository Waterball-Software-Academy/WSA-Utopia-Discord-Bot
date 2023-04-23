package tw.waterballsa.utopia.knowledgeking.domain

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.knowledgeking.domain.Question.QuestionType
import tw.waterballsa.utopia.knowledgeking.kits.TestKit.Companion.expect


internal class KnowledgeKingTest {
    private val questions = mutableListOf(
            Question(number = 1,
                    description = "關於建構子（constructor）的敘述，下列何者錯誤？",
                    options = listOf(
                            "建構子是與類別同名的方法",
                            "當物件建立時，其成員由建構子方法初始化",
                            "建構子必須指定傳回型態或傳回值",
                            "建構子可以覆載，以提供初始類別物件的各式方法"
                    ),
                    type = QuestionType.SINGLE,
                    answer = SingleAnswerSpec(2),
                    explanation = "建構子無須指定傳回型態或傳回值。"),
            Question(number = 2,
                    description = "下列有關 Java 介面(interface)的敘述何者錯誤？",
                    options = listOf(
                            "所有介面宣告的變數都是 static 變數",
                            "所有介面宣告的方法都是 static 方法",
                            "所有介面宣告的變數都是 final 變數",
                            "所有介面宣告的方法都是 abstract 方法"
                    ),
                    type = QuestionType.SINGLE,
                    answer = SingleAnswerSpec(1)))

    private val quiz = Quiz("Java", questions)

    private var game: KnowledgeKing = KnowledgeKing(quiz, 3)


    @AfterEach
    fun reset() {
        game = KnowledgeKing(quiz, 3)
    }

    @Test
    fun test() {
        expect { game.startContest() }
                .thenRaise(ContestStartedEvent::class.java) {
                    assertEquals(questions.size, it.numberOfQuestions)
                }
                .thenRaise(NextQuestionEvent::class.java) {
                    assertEquals(1, it.question.number)
                    assertEquals(questions[0], it.question)
                    assertFalse(it.isLastQuestion)
                }.thenRaiseNothing()

        assertFalse(game.isGameOver())

        assertEquals(questions[0], game.currentQuestion)

        answer("A", 2) // correct
        answer("B", 3)
        answer("C", 2) // correct
        answer("D", 0)

        game.nextQuestion()!!.let {
            assertEquals(2, it.question.number)
            assertEquals(questions[1], it.question)
            assertTrue(it.isLastQuestion)
        }

        answer("A", 1) // correct
        answer("B", 1) // correct
        answer("C", 1) // correct
        answer("D", 1) // correct

        assertNull(game.nextQuestion(), "no next question")
        val ranking = game.endGame()
        assertTrue(game.isGameOver())

        assertEquals("A", ranking.rank(0).contestantId)
        assertEquals("C", ranking.rank(1).contestantId)
        assertEquals("B", ranking.rank(2).contestantId)
        assertEquals("D", ranking.rank(3).contestantId)

    }

    private fun answer(contestantId: String, singleChoice: Int) {
        val answer = (game.currentQuestion!!.answer as SingleAnswerSpec).optionNumber
        verifyAnsweredEvent(game.answer(contestantId, SingleChoiceAnswer(singleChoice)),
                contestantId = contestantId,
                singleChoiceNumber = 1,
                answerResult = if (answer == singleChoice) AnswerResult.CORRECT else AnswerResult.WRONG)
    }

    private fun verifyAnsweredEvent(
            it: AnsweredEvent, contestantId: String,
            answerResult: AnswerResult,
            singleChoiceNumber: Int? = null,
            multipleChoicesNumbers: Array<Int>? = null,
    ) {
        when (it.answer) {
            is SingleChoiceAnswer -> {
                // TODO
//                assertEquals(singleChoiceNumber!!, it.answer.optionNumber)
            }

            is MultipleChoicesAnswer -> {
//                assertEquals(multipleChoicesNumbers!!, it.answer.optionNumbers)
            }

            else -> throw IllegalStateException("answer type unsupported: ${it.answer::class.java.name}")
        }
        assertNotNull(it.answer.timestamp)
        assertEquals(contestantId, it.contestantId)
        assertEquals(answerResult, it.answerResult)
    }

}

