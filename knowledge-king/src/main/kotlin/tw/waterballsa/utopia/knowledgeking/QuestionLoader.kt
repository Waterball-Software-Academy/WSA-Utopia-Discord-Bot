package tw.waterballsa.utopia.knowledgeking

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.FileNotFoundException


// TODO: 加入 Ratio
class QuestionLoader {
    private val mainDirName = "question"
    private val _mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private var _questions = listOf<QuestionData>()

    init {
        topics = listOf(
            "question/compression.algorithm.json",
            "question/data.structure.json",
            "question/machine.learning.algorithm.json",
            "question/number.theory.algorithm.json",
            "question/search.algorithm.json",
            "question/search.graph.algorithm.json",
            "question/sort.algorithm.json",
            "question/string.algorithm.json",
            "question/synchronization.algorithm.json"
        )
            .map { this.javaClass.classLoader.getResourceAsStream(it) }
            .map { _mapper.readValue(it, TopicData::class.java) }
            .flatMap { it.enable }
    }

    private fun loadJsonFile(path: String): List<File> {
        when {
            File(path).exists() -> return File(path).listFiles()?.filter {
                it.isFile && it.extension == "json" && it.canRead()
            } ?: emptyList()

            else -> throw FileNotFoundException("Cannot find path: $path")
        }
    }

    fun getQuestions(): List<String> {
        return _questions.flatMap {
            it.enable
        }
    }

    class QuestionData {
        val enable: List<String> = listOf()
        val disable: List<String> = listOf()
    }
}



