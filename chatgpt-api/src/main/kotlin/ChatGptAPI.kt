import ChatGptAPI.Message
import ch.qos.logback.core.util.OptionHelper.getEnv
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

val log = KotlinLogging.logger {}

class ChatGptAPI() {
    private val _mapper: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val endpoint = "https://api.openai.com/v1/chat/completions"
    private val _model = "gpt-3.5-turbo"
    private val _maxTokens = 2500
    private val _temperature: Double = 0.0
    private var secret: String? = null

    init {
        secret = try {
            readSecret()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun readSecret(): String {
        return getEnv("CHATGPT_TOKEN")
    }

    fun chat(messages: Array<Message>): Response {
        try {
            val requestBody = _mapper.writeValueAsString(Request(1, _model, messages, _maxTokens, _temperature))
            val request = chatApiRequest(requestBody)
            val response = sendRequest(request)
            val res: Response = _mapper.readValue(response.body(), Response::class.java)
            log.info { "completeTokens: ${res.usage.completion_tokens}, prompt: ${res.usage.prompt_tokens} totalTokens: ${res.usage.total_tokens}" }
            return res
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    private fun chatApiRequest(requestBody: String): HttpRequest {
        return HttpRequest.newBuilder()
                .uri(URI(endpoint))
                .POST(BodyPublishers.ofString(requestBody))
                .header("Authorization", String.format("Bearer %s", secret))
                .header("Content-Type", "application/json")
                .build()
    }

    private fun sendRequest(request: HttpRequest): HttpResponse<String> {
        return HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString())
    }

    data class Request(
            val n: Int, // How many chat completion choices to generate for each input message.
            val model: String,
            val messages: Array<Message>,
            val max_tokens: Int,
            val temperature: Double
    )

    data class Response(
            val id: String = "",
            val `object`: String = "",
            val created: Long = 0,
            val model: String = "",
            val usage: Usage = Usage(),
            val choices: List<Choice> = emptyList()
    )

    data class Usage(
            val prompt_tokens: Int = 0,
            val completion_tokens: Int = 0,
            val total_tokens: Int = 0
    )

    data class Choice(
            val message: Message = Message(),
            val finish_reason: String = "",
            val index: Int = 0
    )

    data class Message(
            val role: String = "",
            val content: String = ""
    )

}

fun main() {
    val chatGptAPI = ChatGptAPI()
    val messages = arrayOf(Message("system", """
        你現在是一個「技術論壇的經營專家」，請針對貼文的分類，對該貼文給予「評論」，請不要針對貼文內容做任何回覆。
        
        你必須遵守以下「評論的規則」：
        1. 首先你要先判斷此文章是「提問」、「知識分享」，還是「開話題」。
        2. 如果是「知識分享」的話，遵照以下格式回覆誇獎："謝謝你的分享，此文的摘要為 <摘要>，<阿諛諂媚的誇獎>。"
        3. 如果是「提問」的話，請用 "How-To-Ask-Questions-The-Smart-Way" 文章中提到的發問要點，針對每一個要點來評分、提出可優化的建議，並遵照以下格式回覆："嗨 :grinning: 我參考 How-To-Ask-Questions-The-Smart-Way 這篇文章中的指示，給您一些提問上的小建議：<針對每一個要點給評論，每一個要點一行文字，每一行中先列出要點名稱，接著顯示此貼文在此要點上獲得的評分(滿分 10 分），評分用小括弧包著，如：(6/10 分)，評分後面加一個冒號'：'，接著從分數多寡來給不同建議，如果分數少於等於 6 分，提出改進建議，如果高於 6 分，給予大力誇獎>"，結尾再標上總分，總分後面立刻結束回應。
        4. 如果是「開話題」的話，那麼你就只要針對這個話題直接用高貴紳士的語氣回覆他就好。
        5. 無論是哪種貼文，都直接以「紳士」的口吻回覆就好，不要加上任何官方的解釋，回覆的主要語言為「繁體中文」，針對專有名詞可以適時使用英文。
    """.trimIndent()), Message("user", """
                標題：有沒有人接過門羅幣...
                內文：
                '''
                有辦法透過daemon 利用交易紀錄加總嗎
        
                是不是2017年後就做不到這件事了?
        
                门罗币是不能隐匿交易金额的，直到 2015 年，数学博士 Shen Noether 发布了一篇研究文章，为“隐匿交易“提供了理论依据。到 2017 年 9 月，开发团队在对门罗币进行硬分叉时整合进了该技术。它的作用在于，当乙要花费存在一次性地址中的资产时，资金数目将自动分解输出，并且每一部分都与链上记录的“输出“不同，从而让人无法得知该次转账的实际金额。
        
                這樣感覺是不是一定要有帳密才能得到餘額   ps 我只要餘額 其他啥都不用
                '''
            """.trimIndent()))
    val result = chatGptAPI.chat(messages)
    print(result)
}