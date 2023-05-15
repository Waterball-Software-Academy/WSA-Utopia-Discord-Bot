package tw.waterballsa.utopia.chatgpt

import ch.qos.logback.core.util.OptionHelper.getEnv
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import javax.inject.Named

val log = KotlinLogging.logger {}

@Named
class ChatGptAPI {
    private val _mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val endpoint = "https://api.openai.com/v1/chat/completions"
    private val _model = "gpt-3.5-turbo"

    // this is default property of completion tokens
    private val _maxTokens = 2000
    private val _temperature = 0.8
    private var secret: String? = null

    val maxTokens = 4096

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

    /**
     * Consider calculating the total number of tokens at the outside-level, and passing the maximum number of tokens for completion.
     */
    fun chat(messages: Array<Message>, maxTokens: Int = _maxTokens): Response {
        log.info { "[Sending Chat] {\"totalContentLength\": ${messages.sumOf { m -> m.content.length }}}" }
        val requestBody = _mapper.writeValueAsString(
            Request(1, _model, messages, maxTokens, _temperature)
        )
        val httpRequest = chatApiRequest(requestBody)
        val httpResponse = sendRequest(httpRequest)
        val chatGptResponse: Response = _mapper.readValue(httpResponse.body(), Response::class.java)
        if (httpResponse.statusCode() == 200) {
            log.info { "[Completion] {\"completeTokens\": ${chatGptResponse.usage.completion_tokens}, \"prompt\": ${chatGptResponse.usage.prompt_tokens}, \"totalTokens\": ${chatGptResponse.usage.total_tokens}}" }
        } else {
            log.error { "[Error] {\"errorBody\":\"${httpResponse.body()}\"}" }
            if ("server_error" == chatGptResponse.error.type) {
                // TODO: should implement retry logic
            }
        }
        return chatGptResponse
    }

    private fun chatApiRequest(requestBody: String): HttpRequest = HttpRequest.newBuilder()
        .uri(URI(endpoint))
        .POST(BodyPublishers.ofString(requestBody))
        .header("Authorization", String.format("Bearer %s", secret))
        .header("Content-Type", "application/json")
        .build()

    private fun sendRequest(request: HttpRequest): HttpResponse<String> = HttpClient.newBuilder()
        .build()
        .send(request, HttpResponse.BodyHandlers.ofString())

    class Request(
        val n: Int, // How many chat completion choices to generate for each input message.
        val model: String,
        val messages: Array<Message>,
        val max_tokens: Int,
        val temperature: Double
    )

    class Response(
        val id: String = "",
        val `object`: String = "",
        val created: Long = 0,
        val model: String = "",
        val usage: Usage = Usage(),
        val choices: List<Choice> = emptyList(),
        val error: Error = Error()
    ) {
        fun firstMessageContent() = choices[0].message.content
    }

    class Error(
        val message: String = "",
        val type: String = "",
        val param: Long = 0,
        val code: String = ""
    )


    class Usage(
        val prompt_tokens: Int = 0,
        val completion_tokens: Int = 0,
        val total_tokens: Int = 0
    )

    class Choice(
        val message: Message = Message(),
        val finish_reason: String = "",
        val index: Int = 0
    )

    class Message(
        val role: String = "",
        val content: String = ""
    )

}
