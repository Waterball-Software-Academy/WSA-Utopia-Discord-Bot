package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import tw.waterballsa.utopia.utopiagamificationquest.domain.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageReactionCriteria
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.MessageSentCriteria

private const val unlockEmoji = "🔑"

val Quests.unlockAcademyQuest: Quest
    get() = quest {
        title = "任務：解鎖學院"
        description =
            """
            ${wsa.unlockEntryChannelLink}
            到這裡點emoji解鎖哦
            """.trimIndent()

        reward = Reward(
                "已解鎖學院，任務完成",
                100u
        )
        criteria = MessageReactionCriteria(wsa.unlockEntryMessageId, unlockEmoji)

        nextQuest = selfIntroductionQuest
    }

val Quests.selfIntroductionQuest: Quest
    get() = quest {
        title = "任務：自我介紹"
        description =
            """
            ${wsa.selfIntroChannelLink}
            到自我介紹串發一篇自我介紹吧!請依照以下格式
            ```
            【 <您的暱稱> 】 
            **工作職位：** <您的工作職位>
            **公司產業：** <您工作所在公司的產業類型>
            **專長：** <您的專長>
            **興趣：** <您的興趣>
            **簡介**： <您的簡介>
            
            **三件關於我的事，猜猜哪一件是假的**：
            1.
            2.
            3.
            ```
            """.trimIndent()

        reward = Reward(
                "已完成自我介紹，任務完成",
                100u
        )
        criteria = MessageSentCriteria(wsa.selfIntroChannelId, 1, regex = getSelfIntroductionRegex())

        nextQuest = firstMessageActionQuest
    }

private fun getSelfIntroductionRegex(): Regex {
    return """【(.|\n)*】(.|\n)*工作職位：?(:)?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(:)?(.|\n)*興趣：?(:)?(.|\n)*簡介：?(:)?(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegex()
}

val Quests.firstMessageActionQuest: Quest
    get() = quest {
        title = "任務:新生報到"
        description =
                """
            ${wsa.discussionAreaChannelLink}
            到話題閒聊區留言
            """.trimIndent()

        reward = Reward(
                "已完成閒聊區第一次留言!!",
                100u,
        )

        criteria = MessageSentCriteria(wsa.discussionAreaChannelId, 1)

        nextQuest = SendContainsImageMessageInEngineerLifeChannelQuest
    }

val Quests.SendContainsImageMessageInEngineerLifeChannelQuest: Quest
    get() = quest {
        title = "任務:工程師生活"
        description =
                """
            ${wsa.engineerLifeChannelLink}
            到工程師生活發布一張生活照片吧
            """.trimIndent()

        reward = Reward(
                "已發布照片!!",
                100u,
        )

        criteria = MessageSentCriteria(wsa.engineerLifeChannelId, 1, containsImage = true)

        nextQuest = ReplyToAnyoneInCareerAdvancementTopicChannelQuest
    }

val Quests.ReplyToAnyoneInCareerAdvancementTopicChannelQuest: Quest
    get() = quest {
        title = "任務:職涯攻略話題"
        description =
                """
            ${wsa.careerAdvancementTopicChannelLink}
            到職涯公略區回復其他人的訊息八
            """.trimIndent()

        reward = Reward(
                "已回復訊息!!",
                100u,
        )

        criteria = MessageSentCriteria(wsa.careerAdvancementTopicChannelId, 1, isReplied = true)

        nextQuest = SendMessageInVoiceChannelQuest
    }

const val anyChannel = ""

val Quests.SendMessageInVoiceChannelQuest: Quest
    get() = quest {
        title = "任務:吃瓜社團會議間"
        description =
                """
            參與任一個當前人數大於兩人的語音頻道，並在 Chat 中發表 1 則訊息
            """.trimIndent()

        reward = Reward(
                "已發表一則訊息!!",
                100u,
        )

        criteria = MessageSentCriteria(anyChannel, 1, voicePopulation = 2)

    }


