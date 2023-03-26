package tw.waterballsa.utopia.chatgpt

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
import javax.inject.Named

val log = KotlinLogging.logger {}

@Named
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

