package tw.waterballsa.utopia.chatgpt

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingType

/**
 * use with OpenAI's GPT-3.5 models.
 */
class JTokkit {
    private val registry = Encodings.newDefaultEncodingRegistry()
    private val encodingType: EncodingType = EncodingType.CL100K_BASE

    fun measureNumOfTokens(string: String): Int {
        val encoding: Encoding = registry.getEncoding(encodingType)
        val encoded: List<Int> = encoding.encode(string)
        return encoded.size
    }
}
