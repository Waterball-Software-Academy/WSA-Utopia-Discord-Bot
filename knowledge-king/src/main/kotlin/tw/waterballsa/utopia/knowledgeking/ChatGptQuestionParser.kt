package tw.waterballsa.utopia.knowledgeking

import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.chatgpt.JTokkit
import tw.waterballsa.utopia.knowledgeking.domain.Question
import tw.waterballsa.utopia.knowledgeking.domain.SingleAnswerSpec
import java.util.*

@Configuration
open class ChatGptQuestionParserConfig {
    @Bean
    open fun chatGptQuestionParser(chatGptAPI: ChatGptAPI, questionLoader: QuestionLoader) = ChatGptQuestionParser(chatGptAPI, questionLoader)
}

private const val RANDOM_SEED_LENGTH = 50

private val log = KotlinLogging.logger {}

// TODO: 下一版移除
private val topics = listOf(
    "資料結構和演算法",
    "資料庫管理系統",
    "網路安全",
    "雲端運算",
    "物聯網（IoT）",
    "人工智慧（AI）",
    "機器學習（Machine Learning）",
    "深度學習（Deep Learning）",
    "自然語言處理（NLP）",
    "運算模型",
    "編譯器設計",
    "計算複雜度",
    "計算理論",
    "離散數學",
    "電腦網路",
    "網際網路協定（IP）",
    "網際網路路由協定（TCP/IP）",
    "網際網路標準（IETF）",
    "網際網路安全協定（IPSec）",
    "網路通訊協定（HTTP、FTP、SMTP）",
    "資訊科技 (Information Technology)",
    "網路技術 (Networking)",
    "虛擬實境 (Virtual Reality)",
    "嵌入式系統 (Embedded Systems)",
    "軟體工程 (Software Engineering)",
    "計算機圖形學 (Computer Graphics)",
    "計算機結構 (Computer Architecture)",
    "作業系統 (Operating Systems)",
    "電腦視覺 (Computer Vision)",
    "資料科學 (Data Science)",
    "電腦音樂 (Computer Music)",
    "人機互動 (Human-Computer Interaction)",
    "網頁設計",
    "程式語言",
    "系統編程",
    "大型系統設計",
    "雲端存儲",
    "虛擬化技術",
    "容器化技術",
    "資訊安全",
    "網路攻擊與防禦",
    "密碼學",
    "防火牆",
    "病毒與惡意軟體",
    "應用程式安全",
    "系統安全",
    "網路監視",
    "資料庫系統",
    "關聯式資料庫",
    "非關聯式資料庫",
    "NoSQL 資料庫",
    "SQL 語言",
    "資料庫優化",
    "資料備份與還原",
    "資料庫安全",
    "雲端運算",
    "傳統 IT 架構",
    "開放原始碼",
    "版本控制",
    "套件管理",
    "自動化測試",
    "敏捷開發方法學",
    "jQuery",
    "Bootstrap",
    "React",
    "AngularJS",
    "Vue.js",
    "Ember.js",
    "Sass (Syntactically Awesome Style Sheets)",
    "LESS (Leaner Style Sheets)",
    "Webpack",
    "Babel",
    "Grunt",
    "Gulp",
    "Handlebars.js",
    "Mustache.js",
    "Backbone.js",
    "Polymer",
    "Ruby on Rails",
    "Node.js",
    "Django (Python)",
    "Flask (Python)",
    "Express.js",
    "ASP.NET",
    "Laravel (PHP)",
    "Spring (Java)",
    "Hibernate (Java)",
    "Scala",
    ".NET Framework",
    "MySQL",
    "MongoDB",
    "PostgreSQL",
    "Oracle Database",
    "SQL Server",
    "基礎設施即代碼 (Infrastructure as Code, IaC)",
    "自動化部署 (Continuous Deployment, CD)",
    "自動化集成 (Continuous Integration, CI)",
    "自動化監控 (Continuous Monitoring)",
    "建置管理 (Build Management)",
    "日誌管理 (Log Management)",
    "可擴展性 (Scalability)",
    "負載平衡 (Load Balancing)",
    "高可用性 (High Availability)",
    "監控與追蹤 (Monitoring and Tracing)",
    "錯誤追蹤 (Error Tracking)",
    "自動化配置管理 (Automated Configuration Management)",
    "DevOps 文化 (DevOps Culture)",
    "Apache HTTP Server",
    "Nginx",
    "Microsoft IIS (Internet Information Services)",
    "Gunicorn",
    "Tomcat",
    "冒泡排序",
    "插入排序",
    "選擇排序",
    "快速排序",
    "合併排序",
    "搜尋演算法",
    "線性搜尋",
    "二元搜尋",
    "深度優先搜尋",
    "廣度優先搜尋",
    "圖論演算法 (Graph algorithms)",
    "最短路徑演算法",
    "最小生成樹演算法",
    "拓撲排序演算法",
    "最大流演算法",
    "字符串演算法 (String algorithms)",
    "字串匹配演算法",
    "字串搜索演算法",
    "字串排序演算法",
    "數論演算法 (Number theory algorithms)",
    "質數判定演算法",
    "費馬小定理",
    "歐拉函數",
    "最大公因數",
    "最小公倍數",
    "壓縮演算法 (Compression algorithms)",
    "漢弗曼編碼",
    "LZ77",
    "LZ78",
    "DEFLATE",
    "機器學習演算法 (Machine learning algorithms)",
    "決策樹",
//  "神經網路",
//  "支援向量機",
    "隨機森林",
    "K-均值",
    "同步演算法 (Concurrency algorithms)",
    "信號量",
    "條件變量",
    "讀寫鎖",
    "死鎖避免",
)

private val programTopics = listOf(
//  "Ada 語言",
    "Assembly language 語言",
    "BASIC 語言",
    "C 語言",
    "C++ 語言",
    "COBOL 語言",
//  "Crystal 語言",
    "CSS 語言",
    "Dart 語言",
//  "Delphi 語言",
//  "Elixir 語言",
    "Erlang 語言",
    "Fortran 語言",
    "Go 語言",
    "Groovy 語言",
    "Haskell 語言",
    "HTML 語言",
    "Java 語言",
    "JavaScript 語言",
    "Julia 語言",
    "Kotlin 語言",
    "Lisp 語言",
    "Lua 語言",
    "MATLAB 語言",
    "Objective-C 語言",
    "Pascal 語言",
    "Perl 語言",
    "PHP 語言",
    "PowerShell 語言",
    "Prolog 語言",
    "Python 語言",
    "R 語言",
    "Ruby 語言",
    "Rust 語言",
//  "SAS 語言",
//  "Scala 語言",
//  "Scheme 語言",
    "Shell script 語言",
    "SQL 語言",
    "Swift 語言",
    "TypeScript 語言",
    "Visual Basic 語言"
)

private val random = Random()

class ChatGptQuestionParser(private val chatGptAPI: ChatGptAPI, private val questionLoader: QuestionLoader) {
    private val questionQueue = QuestionQueue<String>()
    private val jTokkit = JTokkit()
    private var basedUsageTokens = 2000
    private val programTopicRatio = 0.2

    private val completionContentFormat: String = """
        --Question--
        1. 這是一個用來模擬題目大概的會是佔多少 TOKEN 的問題？佔位文字佔位文字佔位文字佔位文字佔位文字佔位文字
        --Options--
        A) 這是一個模擬選項 A 大概會佔多少 TOKENS，佔位文字佔位文字佔位文字佔位文字佔位文字佔位文字
        B) 這是一個模擬選項 B 大概會佔多少 TOKENS，佔位文字佔位文字佔位文字佔位文字佔位文字佔位文字
        C) 這是一個模擬選項 C 大概會佔多少 TOKENS，佔位文字佔位文字佔位文字佔位文字佔位文字佔位文字
        D) 這是一個模擬選項 D 大概會佔多少 TOKENS，佔位文字佔位文字佔位文字佔位文字佔位文字佔位文字
        答案：A
    """

    // TODO: 範例程式碼、難度調整
    private fun generateQuestionContent(
        numberOfQuestions: Int,
        topicsString: String,
        pastQuestionsString: String
    ): String = """
            你現在是知識競賽出題達人。
            你幫忙出選擇題，題目包含「題目」和「四個選項」，並且每一題都有既定的答案。
            
            出題原則：
            1. 總共 $numberOfQuestions 題
            2. 題目類型為：$topicsString，難度依題目順序遞增。
            3. 純粹的名詞解釋題越少越好，專有名詞跟術語可以保持英文。
            4. 若為程式語言的題目時，至少出一題「有附上實際程式案例」或「實際的情境」的題目。
            5. 語言：題目答案皆使用繁體中文，
            6. 選項一律四個，選項不得重複
            7. 題目不要類似：$pastQuestionsString
            
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
        """.trimIndent()

    fun generateQuestions(topic: String, numberOfQuestions: Int): List<Question> {
        val shuffledTopics = getRandomTopics(numberOfQuestions)

        log.info { "[Generate questions] {\"topics\": ${shuffledTopics.joinToString("、")}}" }

        val basedUserContentTokens = getTokens(
            generateQuestionContent(
                numberOfQuestions,
                topics.sortedByDescending { it.length }.take(5).map { "「$it」" }.joinToString { "、" },
                ""
            )
        )
        val completionUsageTokens = getTokens(completionContentFormat.trimIndent()) * numberOfQuestions

        basedUsageTokens = basedUserContentTokens + completionUsageTokens

        // TODO: 有可能會有 empty questions 的狀態，先加入 retry 機制，後續可以改進
        val questionList = executeWithRetry(retries = 3, call = {
            val response = chatGptAPI.chat(
                arrayOf(ChatGptAPI.Message("user", generateQuestionContent(
                    numberOfQuestions,
                    shuffledTopics.joinToString("、") { "「$it」" },
                    questionQueue.all().joinToString("、") { "「$it」" }
                ))),
                completionUsageTokens
            )
            val questions = parse(response)
            when {
                questions.isNotEmpty() -> questions
                else -> throw NullPointerException("Failed to load data from ChatGptAPI")
            }
        })
        questionList!!.forEach { addQuestion(it) }
        return questionList
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

        // generateRandomSeed(RANDOM_SEED_LENGTH)

        questionRawList.forEachIndexed { index, rawString ->
            val parts = rawString.trimIndent().split("--Options--")
            val description = parts[0].replace("""^\d+\.\s+""".toRegex(), "").trim()
            var optionRawString: String = ""
            optionRawString = parts[1].replace("""^\d+\.\s+""".toRegex(), "").trim()

            // "答案：{A-D}" take last word
            val optionNumber = charToNumber[optionRawString.trim().lines()[4].trim().lastOrNull().toString()]
            optionNumber?.let {
                questions.add(
                    Question(
                        index + 1,
                        description,
                        optionRawString.lines().subList(0, 4).map {
                            val option = it.replace("""^([A-D]\)\s)""".toRegex(), "").trim()
                            option
                        },
                        Question.QuestionType.SINGLE,
                        SingleAnswerSpec(optionNumber)
                    )
                )
            }
        }
        return questions
    }

    private fun getRandomTopics(takeNumber: Int): List<String> {
        return questionLoader.getTopics().shuffled().take(takeNumber)
    }

    private fun getTokens(string: String): Int {
        return jTokkit.measureNumOfTokens(string)
    }

    private fun addQuestion(question: Question) {
        questionQueue.enqueue(question.description)

        // TODO: 暫時用 queue 控制長度，避免 token 值超過 4096
        ensureQuestionsTokensLessThenMaxTokens()
    }

    /**
     * ensure user content's tokens + completion content's < 4096
     */
    private fun ensureQuestionsTokensLessThenMaxTokens() {
        val questionsString = questionQueue.all().joinToString("、") { "「$it」" }
        if (chatGptAPI.maxTokens < basedUsageTokens + getTokens(questionsString)) {
            questionQueue.dequeue()
            ensureQuestionsTokensLessThenMaxTokens()
        }
    }

//    private fun generateRandomSeed(length: Int): String {
//        val letters = ('a'..'z')
//        val sb = StringBuilder()
//
//        repeat(length) {
//            sb.append(letters.random())
//        }
//        return sb.toString()
//    }
}

inline fun <T> executeWithRetry(
    predicate: (cause: Throwable) -> Boolean = { true },
    retries: Int = 1,
    call: () -> T
): T? {
    for (i in 0..retries) {
        return try {
            call()
        } catch (e: Exception) {
            when {
                predicate(e) && i < retries -> continue
                else -> throw e
            }
        }
    }
    return null
}


private class QuestionQueue<T> {

    // TODO: 後續可以考慮其他資料持久化實作
    private val elements: MutableList<T> = mutableListOf()

    fun enqueue(element: T) {
        log.info { "[Add past questions] {\"question\": ${element}}" }

        elements.add(element)
    }

    fun dequeue(): T? {
        if (isEmpty()) {
            return null
        }

        log.info { "[Remove past questions] {\"question\": ${elements[0]}}" }

        return elements.removeAt(0)
    }

    fun all(): List<T> {
        return elements
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }
}
