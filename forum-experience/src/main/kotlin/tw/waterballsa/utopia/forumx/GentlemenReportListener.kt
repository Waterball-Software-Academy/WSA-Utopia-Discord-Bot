package tw.waterballsa.utopia.forumx

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.createFileIfNotExists
import tw.waterballsa.utopia.jda.UtopiaListener
import java.io.File
import java.nio.file.Files.lines
import java.nio.file.Files.writeString
import java.nio.file.Paths

private val log = KotlinLogging.logger {}

private const val RECENT_MESSAGES_THRESHOLD = 4
private const val DATABASE_FILENAME = "data/forumx.gentlemen-broadcast.db"
private val DATABASE_FILE_LOCK = Any()

private const val chatGptReportEnchantmentOnNewPost = """
        你現在是一個社群中的專業記者，你要幫忙摘要每一篇新貼文。
         
        你的回應遵守以下格式：
        ```
        嗨！我是記者 Waterball！有人貼了一則關於<20~30 字內文摘要>的貼文，描述著「 <列出 5~10 項此貼文中的重點>」。
        
        <針對貼文內文，如果此貼文為「知識分享」則稱讚貼文作者並呼籲大家觀看內文、如果此貼文為「提問」，則呼籲大家來幫忙回覆>。
        ```
    """

private const val chatGptReportEnchantmentOnNewComments = """
        你現在是一個社群中的專業記者，你觀察人們在論壇中的互動。

        你的回應遵守以下格式：
        ```
        嗨！我是記者 Waterball！人們正在這則貼文中討論著「 <以你的角度總結他們的"對話內容"，至少 15 個字，匿名化>」。

        <針對留言的對話內容，給一些幽默的評語，呼籲大家來觀看此則貼文內容>。
       
    """

@Component
class GentlemenReportListener(private val wsa: WsaDiscordProperties,
                              private val chatGptApi: ChatGptAPI,
                              private val jda: JDA) : UtopiaListener() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        with(event) {
            if (message.channel !is ThreadChannel) {
                return
            }
            val post = message.channel.asThreadChannel()
            val parentChannel = post.parentChannel

            if (parentChannel !is ForumChannel) {
                return
            }

            val forum = parentChannel.asForumChannel()
            if (forum.parentCategoryId != wsa.gentlemenForumCategoryId) {
                return
            }

            if (post.messageCount == 1) {
                // new post
                val summary = summarizeNewPostContent(post, message)
                val gentlemenBroadcastChannel = jda.getTextChannelById(wsa.wsaGentlemenBroadcastChannelId)!!
                broadcast(gentlemenBroadcastChannel, post, message, summary)
                updateLastSeenMessage(post, message)
            } else {
                // new comment in a post
                post.getHistoryBefore(message.id, RECENT_MESSAGES_THRESHOLD)
                        .queue { history ->
                            summarizeTheMostRecentMessagesIfPassNumberThreshold(message, history.retrievedHistory, post)
                        }
            }
        }
    }


    private fun summarizeTheMostRecentMessagesIfPassNumberThreshold(newMessage: Message,
                                                                    recentMessages: List<Message>,
                                                                    post: ThreadChannel) {
        val lastSeenMessageId = getLastSeenMessageIdInPost(post.id)
        // check if the number of new unseen messages passes the threshold
        if (recentMessages.none { it.id == lastSeenMessageId }) {
            post.getHistoryFromBeginning(1)
                    .queue { historyFromBeginning ->
                        val postContent = historyFromBeginning.retrievedHistory[0]
                        val response = summarizeByChatGpt(post, recentMessages)
                        val gentlemenBroadcastChannel = jda.getTextChannelById(wsa.wsaGentlemenBroadcastChannelId)!!
                        broadcast(gentlemenBroadcastChannel, post, newMessage, response)
                        updateLastSeenMessage(post, newMessage)
                    }
        }
    }

    private fun getLastSeenMessageIdInPost(postChannelId: String): String? =
            synchronized(DATABASE_FILE_LOCK) {
                lines(File(DATABASE_FILENAME).createFileIfNotExists())
                        .map { it.split(":") }
                        .filter { it[0] == postChannelId }
                        .map { it[1] }.findFirst().orElse(null)
            }

    private fun summarizeByChatGpt(post: ThreadChannel, recentMessages: List<Message>): String {
        val conversation = recentMessages.joinToString("\n")
        { "${it.author.name}: ${it.contentRaw.take(100)}" }

        val messages = arrayOf(ChatGptAPI.Message("system", chatGptReportEnchantmentOnNewComments.trimIndent()), ChatGptAPI.Message("user",
                """
            標題：'${post.name}'
            對話內容：
            '''
            $conversation
            '''
            """.trimIndent()))

        val response = chatGptApi.chat(messages)
        return response.firstMessageContent()
    }

    private fun broadcast(gentlemenBroadcastChannel: TextChannel,
                          postChannel: ThreadChannel,
                          message: Message,
                          response: String) {
        gentlemenBroadcastChannel.sendMessage(
                "【貼文】<#${postChannel.id}>\n【訊息】${message.jumpUrl}\n\n$response")
                .queue { log.info { "[Gentlemen Broadcast] {\"message\":\"${it.contentRaw}\"}" } }
    }

    private fun updateLastSeenMessage(post: ThreadChannel, message: Message) {
        synchronized(DATABASE_FILE_LOCK) {
            val map = readPostIdToLastSeenMessageIdPairsFromDB(post, message)
            map[post.id] = message.id
            val newDbContent = map.entries
                    .joinToString("\n") { "${it.key}:${it.value}" }
            writeString(Paths.get(DATABASE_FILENAME), newDbContent)
        }
    }

    private fun readPostIdToLastSeenMessageIdPairsFromDB(post: ThreadChannel, message: Message): MutableMap<String, String> =
            lines(File(DATABASE_FILENAME).createFileIfNotExists())
                    .map { it.split(":") }
                    .map {
                        val postId = it[0]
                        val lastSeenMessageId = if (postId == post.id) message.id else it[1]
                        postId to lastSeenMessageId
                    }.toList()
                    .toMap().toMutableMap()

    private fun summarizeNewPostContent(post: ThreadChannel, postContent: Message): String {
        val messages = arrayOf(ChatGptAPI.Message("system", chatGptReportEnchantmentOnNewPost.trimIndent()), ChatGptAPI.Message("user", """
                標題：'${post.name}'
                貼文內文：
                '''
                ${postContent.contentRaw.take(500)}
                ...
                '''
            """.trimIndent()))

        val response = chatGptApi.chat(messages)
        return response.firstMessageContent()
    }

}
