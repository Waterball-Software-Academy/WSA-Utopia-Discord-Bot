package tw.waterballsa.utopia.automaticchannelcommenttracking.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.automaticchannelcommenttracking.domain.UsersMessageCount
import java.io.File


class CommentCountRepository(
    private val jsonMapper: ObjectMapper
) {
    private val jsonFile = File("data/role.json")
    private val module = SimpleModule()


    init {
        module.addKeyDeserializer(Query::class.java, QueryDeserializer())
        jsonMapper.registerModule(module)
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT)

        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
            val jsonData = mutableMapOf<Query, Int>()
            jsonMapper.writeValue(jsonFile, jsonData)
        }
    }

    fun incrementCountByQuery(query: Query) {
        val jsonData = readJsonData().ifEmpty { return }

        jsonData.merge(query, 1) { oldValue, increment -> oldValue + increment }

        writeJsonData(jsonData)
    }

    fun findByQuery(query: Query): UsersMessageCount {
        val jsonData = readJsonData().ifEmpty { return UsersMessageCount() }

        val messageCount = jsonData.filter { query.match(it.key) }.values.sum()

        return query.toUsersMessageCount(messageCount)
    }

    fun removeByQuery(query: Query) {
        val jsonData = readJsonData().ifEmpty { return }
        jsonData.entries.removeIf { query.match(it.key) }
        writeJsonData(jsonData)
    }

    private fun readJsonData(): MutableMap<Query, Int> =
        jsonMapper.readValue(jsonFile, object : TypeReference<LinkedHashMap<Query, Int>>() {}) ?: mutableMapOf()

    private fun writeJsonData(jsonData: MutableMap<Query, Int>) = jsonMapper.writeValue(jsonFile, jsonData)
}

data class Query(
    private val date: String = IGNORE,
    private val userId: String = IGNORE,
    private val channelId: String = IGNORE
) {
    companion object {
        const val IGNORE = ""
    }

    fun match(query: Query): Boolean {
        return query.date.contains(date) &&
                query.userId.contains(userId) &&
                query.channelId.contains(channelId)
    }

    fun toUsersMessageCount(messageCount: Int): UsersMessageCount =
        UsersMessageCount(date, userId, channelId, messageCount)
}


private class QueryDeserializer : KeyDeserializer() {

    override fun deserializeKey(jsonString: String, ctxt: DeserializationContext): Any {
        val date = jsonString.substringAfter("date=").substringBefore(",")
        val userId = jsonString.substringAfter("userId=").substringBefore(",")
        val channelId = jsonString.substringAfter("channelId=").substringBefore(")")

        return Query(date, userId, channelId)
    }
}
