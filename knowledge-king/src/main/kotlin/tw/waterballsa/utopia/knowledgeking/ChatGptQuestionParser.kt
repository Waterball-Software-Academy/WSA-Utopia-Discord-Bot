package tw.waterballsa.utopia.knowledgeking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.knowledgeking.domain.Question
import tw.waterballsa.utopia.knowledgeking.domain.SingleAnswerSpec
import java.util.*

@Configuration
open class ChatGptQuestionParserConfig {
    @Bean
    open fun chatGptQuestionParser(chatGptAPI: ChatGptAPI) = ChatGptQuestionParser(chatGptAPI)
}

private const val RANDOM_SEED_LENGTH = 50

private val topics = listOf("資料結構和演算法", "資料庫管理系統", "網路安全", "雲端運算", "物聯網（IoT）", "人工智慧（AI）", "機器學習（Machine Learning）", "深度學習（Deep Learning）", "自然語言處理（NLP）", "運算模型", "編譯器設計", "計算複雜度", "計算理論", "離散數學", "電腦網路", "網際網路協定（IP）", "網際網路路由協定（TCP/IP）", "網際網路標準（IETF）", "網際網路安全協定（IPSec）", "網路通訊協定（HTTP、FTP、SMTP）", "網頁設計", "程式語言", "系統編程", "大型系統設計", "雲端存儲", "虛擬化技術", "容器化技術", "資訊安全", "網路攻擊與防禦", "密碼學", "防火牆", "病毒與惡意軟體", "應用程式安全", "系統安全", "網路監視", "資料庫系統", "關聯式資料庫", "非關聯式資料庫", "NoSQL 資料庫", "SQL 語言", "資料庫優化", "資料備份與還原", "資料庫安全", "雲端運算", "傳統 IT 架構", "開放原始碼", "版本控制", "套件管理", "自動化測試", "敏捷開發方法學")
private val random = Random()

class ChatGptQuestionParser(private val chatGptAPI: ChatGptAPI) {

    fun generateQuestions(topic: String, numberOfQuestions: Int): List<Question> {
        val shuffledTopics = topics.shuffled().take(numberOfQuestions)
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
    請你依照這個格式，出 $numberOfQuestions 個題目給我，其中問題依序為：${shuffledTopics.joinToString(",")}主題。
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

        generateRandomSeed(RANDOM_SEED_LENGTH)

        questionRawList.forEachIndexed { index, rawString ->
            val parts = rawString.trimIndent().split("--Options--")
            val description = parts[0].replace("""^\d+\.\s+""".toRegex(), "").trim()
            var optionRawString: String = ""
            optionRawString = parts[1].replace("""^\d+\.\s+""".toRegex(), "").trim()

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

    private fun generateRandomSeed(length: Int): String {
        val letters = ('a'..'z')
        val sb = StringBuilder()

        repeat(length) {
            sb.append(letters.random())
        }
        return sb.toString()
    }
}
