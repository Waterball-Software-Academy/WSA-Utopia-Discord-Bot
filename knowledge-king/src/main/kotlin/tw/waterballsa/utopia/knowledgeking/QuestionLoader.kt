package tw.waterballsa.utopia.knowledgeking

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Named


// TODO: 加入 Ratio
@Named
class QuestionLoader(private val _mapper: ObjectMapper) {
    private var topics: List<String>

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

    fun getTopics(): List<String> {
        return topics
    }

    class TopicData {
        val enable: List<String> = listOf()
        val disable: List<String> = listOf()
    }
}



