package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.QuizButton

private const val unlockEmoji = "🔑"

val Quests.unlockAcademyQuest: Quest
    get() = quest {
        title = "任務：解鎖學院"
        description =
            """
            ${wsa.unlockEntryChannelId.toLink()} 
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
        val content = """
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
        """.trimIndent()

        title = "任務：自我介紹"
        description =
            """
            ${wsa.selfIntroChannelId.toLink()}
            到自我介紹串發一篇自我介紹吧!請依照以下格式
            ```
            $content
            ```
            """.trimIndent()

        reward = Reward(
            "已完成自我介紹，任務完成",
            100u
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.selfIntroChannelId), regexRule = getSelfIntroductionRegex())

        nextQuest = firstMessageActionQuest
    }

private fun getSelfIntroductionRegex(): RegexRule =
    RegexRule("""【(.|\n)*】(.|\n)*工作職位：?(:)?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(:)?(.|\n)*興趣：?(:)?(.|\n)*簡介：?(:)?(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegex())


val Quests.firstMessageActionQuest: Quest
    get() = quest {
        title = "任務:新生報到"
        description =
            """
            ${wsa.discussionAreaChannelId.toLink()}
            到話題閒聊區留言
            """.trimIndent()

        reward = Reward(
            "已完成閒聊區第一次留言！",
            100u,
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.discussionAreaChannelId))

        nextQuest = watchVideoQuest

    }

val Quests.watchVideoQuest: Quest
    get() = quest {
        title = "任務：觀看學院影片"
        description = """
            ${wsa.featuredVideosChannelId.toLink()}
            觀看任一部學院精華影片，並且在該貼文串底下回覆 1 則訊息
        """.trimIndent()
        reward = Reward(
            "已完成觀看學院影片！",
            100u
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.featuredVideosChannelId))

        nextQuest = flagPostQuest
    }


val Quests.flagPostQuest: Quest
    get() = quest {
        title = "全民插旗子"
        description =
            """
            ${wsa.flagPostChannelId.toLink()}
            在全民插旗子頻道發佈一則貼文
            """.trimIndent()
        reward = Reward(
            "已完成插旗子任務！",
            100u
        )
        criteria = PostCriteria(wsa.flagPostChannelId)

        nextQuest = SendContainsImageMessageInEngineerLifeChannelQuest
    }

val Quests.SendContainsImageMessageInEngineerLifeChannelQuest: Quest
    get() = quest {
        title = "任務:工程師生活"
        description =
            """
            ${wsa.engineerLifeChannelId.toLink()}
            到工程師生活發布一張生活照片吧
            """.trimIndent()

        reward = Reward(
            "已發布照片！",
            100u,
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.engineerLifeChannelId), hasImageRule = BooleanRule.TRUE)

        nextQuest = ReplyToAnyoneInCareerAdvancementTopicChannelQuest
    }

val Quests.ReplyToAnyoneInCareerAdvancementTopicChannelQuest: Quest
    get() = quest {
        title = "任務:職涯攻略話題"
        description =
            """
            ${wsa.careerAdvancementTopicChannelId.toLink()}
            到職涯攻略區回覆其他人的訊息吧
            """.trimIndent()

        reward = Reward(
            "已回覆訊息！",
            100u,
        )

        criteria =
            MessageSentCriteria(ChannelIdRule(wsa.careerAdvancementTopicChannelId), hasRepliedRule = BooleanRule.TRUE)

        nextQuest = resumeHealthCheckQuest
    }

val Quests.resumeHealthCheckQuest: Quest
    get() = quest {
        title = "任務：履歷健檢"

        description = """
            ${wsa.resumeCheckChannelId.toLink()}
            在履歷健檢頻道的任一則貼文內回覆 1 則訊息
        """.trimIndent()

        reward = Reward(
            "已回覆訊息！",
            100u
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.resumeCheckChannelId))

        nextQuest = SendMessageInVoiceChannelQuest
    }

val Quests.SendMessageInVoiceChannelQuest: Quest
    get() = quest {
        title = "任務:吃瓜社團會議間"
        description =
            """
            參與任一個當前人數大於兩人的語音頻道，並在 Chat 中發表 1 則訊息
            """.trimIndent()

        reward = Reward(
            "已發表一則訊息！",
            100u,
        )

        criteria = MessageSentCriteria(ChannelIdRule.ANY_CHANNEL, numberOfVoiceChannelMembersRule = AtLeastRule(2))

        nextQuest = JoinActivityQuest
    }

val Quests.JoinActivityQuest: Quest
    get() = quest {
        title = "任務:參加一場活動"
        description =
            """
            參與名稱為 test 的活動，並停留 10 秒
            """.trimIndent()

        reward = Reward(
            "已完成！",
            100u,
        )

        criteria = JoinActivityCriteria("test", 1, 10)
        nextQuest = quizQuest
    }

val Quests.quizQuest: Quest
    get() = quest {
        title = "任務:考試"
        description =
            """
            按下按鈕開始考試並通關。
            """.trimIndent()

        reward = Reward(
            "已通過考試！",
            100u,
        )

        criteria = ButtonInteractionCriteria(QuizButton.NAME)

    }
