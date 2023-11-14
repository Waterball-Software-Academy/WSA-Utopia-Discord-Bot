package tw.waterballsa.utopia.chatgpt

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.ModelType

/**
 * use with OpenAI's GPT-3.5 models.
 */
class JTokkit {
    companion object {
        private val registry = Encodings.newLazyEncodingRegistry()
        private val encoding = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO)
    }

    fun measureNumOfTokens(string: String): Int = encoding.countTokens(string)
}
