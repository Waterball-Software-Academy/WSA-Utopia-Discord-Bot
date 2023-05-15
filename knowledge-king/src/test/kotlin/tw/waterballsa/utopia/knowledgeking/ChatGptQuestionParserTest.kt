package tw.waterballsa.utopia.knowledgeking

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.knowledgeking.domain.Question
import tw.waterballsa.utopia.knowledgeking.domain.SingleAnswerSpec

internal class ChatGptQuestionParserTest {
    private val parser = ChatGptQuestionParser(mock(ChatGptAPI::class.java), mock(QuestionLoader::class.java))

    @Test
    fun testParser() {
        val questions = parser.parse(
                """
                    好的，以下是五個關於「Computer Science」的題目，難度由易到難：

                    --Question--
                    1. 請問以下程式碼輸出的結果為何？
                    ```
                    val x = 2
                    val y = 3
                    val z = x + y
                    print("x + y = z")
                    ```
                    --Options--
                    A) x + y = 5
                    B) x + y = z
                    C) x + y = 2 + 3
                    D) x + y = "2 + 3"
                    答案：C
                    --Question--
                    2. 在程式語言中，什麼是「變數」？
                    --Options--
                    A) 一種數學運算符號
                    B) 一個可儲存數值或資料的儲存區域
                    C) 一個用來顯示圖形的函式
                    D) 一個用來控制程式流程的關鍵字
                    答案：B

                    --Question--
                    3. 下列哪個演算法可以用來找出一個陣列中的最大值？
                    --Options--
                    A) 插入排序（Insertion Sort）
                    B) 快速排序（Quick Sort）
                    C) 選擇排序（Selection Sort）
                    D) 堆積排序（Heap Sort）
                    答案：C

                    --Question--
                    4. 下列哪個選項不是一種網路協議？
                    --Options--
                    A) HTTP
                    B) TCP
                    C) FTP
                    D) HTML
                    答案：D

                    --Question--
                    5. 在 Python 程式語言中，以下哪行程式碼可以用來印出「Hello, World!」？
                    --Options--
                    A) print("Hello, World!")
                    B) console.log("Hello, World!")
                    C) printf("Hello, World!")
                    D) echo "Hello, World!"
                    答案：A
                """.trimIndent()
        )

        assertEquals(Question(1, "請問以下程式碼輸出的結果為何？\n```\nval x = 2\nval y = 3\nval z = x + y\nprint(\"x + y = z\")\n```",
                listOf("x + y = 5", "x + y = z", "x + y = 2 + 3", "x + y = \"2 + 3\""),
                Question.QuestionType.SINGLE, SingleAnswerSpec(2), null),
                questions[0])

        assertEquals(Question(2, "在程式語言中，什麼是「變數」？",
                listOf("一種數學運算符號", "一個可儲存數值或資料的儲存區域", "一個用來顯示圖形的函式", "一個用來控制程式流程的關鍵字"),
                Question.QuestionType.SINGLE, SingleAnswerSpec(1), null),
                questions[1])
        assertEquals(Question(3, "下列哪個演算法可以用來找出一個陣列中的最大值？",
                listOf("插入排序（Insertion Sort）", "快速排序（Quick Sort）", "選擇排序（Selection Sort）", "堆積排序（Heap Sort）"),
                Question.QuestionType.SINGLE, SingleAnswerSpec(2), null),
                questions[2])
        assertEquals(Question(4, "下列哪個選項不是一種網路協議？",
                listOf("HTTP", "TCP", "FTP", "HTML"),
                Question.QuestionType.SINGLE, SingleAnswerSpec(3), null),
                questions[3])
        assertEquals(Question(5, "在 Python 程式語言中，以下哪行程式碼可以用來印出「Hello, World!」？",
                listOf("print(\"Hello, World!\")", "console.log(\"Hello, World!\")", "printf(\"Hello, World!\")", "echo \"Hello, World!\""),
                Question.QuestionType.SINGLE, SingleAnswerSpec(0), null),
                questions[4])
    }
}

