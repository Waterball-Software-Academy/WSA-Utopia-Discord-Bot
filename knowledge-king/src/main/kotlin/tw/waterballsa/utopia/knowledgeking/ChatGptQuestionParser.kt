package tw.waterballsa.utopia.knowledgeking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.knowledgeking.domain.Question
import tw.waterballsa.utopia.knowledgeking.domain.SingleAnswerSpec

@Configuration
open class ChatGptQuestionParserConfig {
    @Bean
    open fun chatGptQuestionParser(chatGptAPI: ChatGptAPI) = ChatGptQuestionParser(chatGptAPI)
}

class ChatGptQuestionParser(private val chatGptAPI: ChatGptAPI) {


    fun generateQuestions(topic: String, numberOfQuestions: Int): List<Question> {
        val response = chatGptAPI.chat(arrayOf(ChatGptAPI.Message("user",
                """
    你現在是知識競賽出題達人。
    你幫忙出選擇題，題目包含「題目」和「四個選項」，並且每一題都有既定的答案。
    
    出題原則：
    1. 純粹的名詞解釋題越少越好，還要出實際的情境題目。
    2. 越多元越好。
    3. 偶爾混雜一些「有附上實際程式案例」的題目。
    4. 偶爾混一些很搞笑、滑稽、腦經急轉彎的題目來讓大家放鬆
    5. 語言：繁體中文
    
    每一題請你依照以下這個格式輸出給我：
    ```
    --Question--
    <題號>. <題目>？
    --Options--
    A) <選項 1 的內容>
    B) <選項 2 的內容>
    C) <選項 3 的內容>
    D) <選項 4 的內容>
    答案：<答案選項編號>
    ```
    請你依照這個格式，出 $numberOfQuestions 個題目給我，主題為「$topic」，難度依題目順序遞增。
        """.trimIndent())))
        return parse(response)
    }

    fun parse(response: ChatGptAPI.Response): List<Question> = parse(response.firstMessageContent())

    fun parse(rawContent: String): List<Question> {
        val questionRawList = rawContent
                .trimIndent()
                .split("--Question--")
                .map {
                    it.trim()
                }.drop(1)

        val questions = mutableListOf<Question>()

        val charToNumber = mapOf(
                "A" to 0,
                "B" to 1,
                "C" to 2,
                "D" to 3,
        )

        questionRawList.forEachIndexed { index, rawString ->
            val parts = rawString.trimIndent().split("--Options--")
            val description = parts[0].replace("""^\d+\.\s+""".toRegex(), "").trim()
            var optionRawString: String = ""
            try {
                optionRawString = parts[1].replace("""^\d+\.\s+""".toRegex(), "").trim()
            } catch (err: IndexOutOfBoundsException) {
                println(err)
            }
            // "答案：{A-D}" take last word
            val optionNumber = charToNumber[optionRawString.trim().lines()[4].trim().lastOrNull().toString()]
            optionNumber?.let {
                questions.add(Question(
                        index + 1,
                        description,
                        optionRawString.lines().subList(0, 4).map {
                            val option = it.replace("""^([A-D]\)\s)""".toRegex(), "").trim()
                            option
                        },
                        Question.QuestionType.SINGLE,
                        SingleAnswerSpec(optionNumber)
                ))
            }
        }
        return questions
    }
}
