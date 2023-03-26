package tw.waterballsa.utopia.forumx

import tw.waterballsa.utopia.chatgpt.ChatGptAPI
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
//
//fun replyWithChatGptAssistantOnEveryNewPost(wsa: WsaDiscordProperties, chatGptApi: ChatGptAPI) = listener {
//    on<MessageCreateEvent> {
//        val c = message.channel
//        val channel = c.asChannel() as TextChannelThread
//        val lastMessageId = channel.lastMessageId
//        val parentId = channel.parentId.value
//
//        if (lastMessageId == firstMessage.id && wsa.gentlemanForumIds.contains(parentId)) {
//            val messages = arrayOf(ChatGptAPI.Message("system", """
//        你現在是一個「技術論壇的經營專家」，請針對貼文的分類，對該貼文給予「評論」，請不要針對貼文內容做任何回覆。
//
//        你必須遵守以下「評論的規則」：
//        1. 首先你要先判斷此文章是「提問」、「知識分享」，還是「開話題」。
//        2. 如果是「知識分享」的話，遵照以下格式回覆誇獎："謝謝你的分享，此文的摘要為 <摘要>，<阿諛諂媚的誇獎>。"
//        3. 如果是「提問」的話，請用 "How-To-Ask-Questions-The-Smart-Way" 文章中提到的發問要點，「非常嚴格地」針對每一個要點來評分、提出可優化的建議，並遵照以下格式回覆："嗨 :grinning: 我參考 How-To-Ask-Questions-The-Smart-Way 這篇文章中的指示，給您一些提問上的小建議：<針對每一個要點給評論，每一個要點一行文字，每一行中先列出要點名稱，接著顯示此貼文在此要點上獲得的評分(滿分 10 分），評分用小括弧包著，如：(6/10 分)，如果認為此項目評分不適用在此貼文上則顯示 (--/--) 不予扣分，評分後面加一個冒號'：'，接著從分數多寡來給不同建議，如果分數少於等於 6 分，提出改進建議，如果高於 6 分，給予大力誇獎>"，結尾再標上總分，總分後面立刻結束回應。
//        4. 如果是「開話題」的話，那麼你就只要針對這個話題直接用高貴紳士的語氣回覆他就好。
//        5. 無論是哪種貼文，都直接以「紳士」的口吻回覆就好，不要加上任何官方的解釋，回覆的主要語言為「繁體中文」，針對專有名詞可以適時使用英文。
//    """.trimIndent()), ChatGptAPI.Message("user", """
//                標題：${channel.name}
//                內文：
//                '''
//                ${message.content}
//                '''
//            """.trimIndent()))
//            val response = chatGptApi.chat(messages)
//            val responseContent = response.choices.first().message.content
//            channel.createMessage(responseContent)
//        }
//    }
//
//
//}