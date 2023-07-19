package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import tw.waterballsa.utopia.utopiagamificationquest.domain.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.QuizButton

private const val unlockEmoji = "🔑"
private const val missionMessage = ">（要是你怕自己的訊息太突兀，只要在訊息的開頭加上 `#任務`，保證自在。）"

fun String.toRegexRule(): RegexRule = RegexRule(this.toRegex())

val Quests.unlockAcademyQuest: Quest
    get() = quest {
        id = 1
        title = "解鎖學院"
        description =
            """
            歡迎你加入水球軟體學院，這裡是最充實又歡樂的軟體社群！
                  
            這裡每週都有學習社團或聚會，你一定能夠在這裡找到更多屬於你的職涯意義，
            你將會學到更多軟體技術知識乾貨，並且認識更多新朋友。

            來吧，為了能夠參加學院中各式各樣的線上聚會，你需要先解鎖學院，只要點個表情符號幾秒內就能解鎖學院囉！
            解鎖後你會獲得基礎的「學院公民」身份。
            
            ${wsa.unlockEntryChannelId.toLink()}
            """.trimIndent()

        preCondition = EmptyPreCondition()

        roleType = RoleType.EVERYONE

        periodType = PeriodType.MAIN_QUEST

        criteria = MessageReactionCriteria(wsa.unlockEntryMessageId, unlockEmoji)

        reward = Reward(
            100u,
            100u,
            1.0f,
            RoleType.WSA_MEMBER
        )

        nextQuest = selfIntroductionQuest
    }

val Quests.selfIntroductionQuest: Quest
    get() = quest {
        id = 2
        title = "自我介紹"
        description =
            """
           來認識新朋友吧！為了讓你在學院中過得更自在一些，我會幫助你融入大家，
           不用擔心，因此這裡設計了一些簡單的新手破冰任務！
           
           大家都會很友善地幫助你完成新手任務的～ 來多認識點朋友吧 ^^
           
           來吧！為了成為學院中的紳士，這裡要開始給你新手任務啦！
           你的首要任務呢，是到 ${wsa.selfIntroChannelId.toLink()} 頻道中和大家簡單地自我介紹！
           
           在學院中，紳士們不分優劣高低更不比較經歷，因此你可以大方地介紹自己～
           對大家而言，每當有人熱情的介紹自己時，大家反而會感到特別開心，因為終於能認識有新朋友了呢！          
           並且呀，這個自我介紹是很有用處的，未來你隨時都能用這份自我介紹訊息來參與各種活動喔！
           
           請複製下方文字範本，到自我介紹頻道和大家介紹自己吧！
            ```【 <你的暱稱> 】 
            **工作職位：** <工作職位>
            **公司產業：** <工作所在公司的產業類型>
            **專長：** <專長>
            **興趣：** <興趣>
            **簡介**： <簡介，至少需填寫 50 個字>

            **三件關於我的事，猜猜哪一件是假的**：
            1.
            2.
            3.
            ```  
            """.trimIndent()
        preCondition = QuestIdPreCondition(1)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.selfIntroChannelId), regexRule = getSelfIntroductionRegex())

        nextQuest = firstMessageActionQuest
    }

private fun getSelfIntroductionRegex(): RegexRule =
    """【(.|\n)*】(.|\n)*工作職位：?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(.|\n)*興趣：?(.|\n)*簡介：?.{50,}(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegexRule()

val Quests.firstMessageActionQuest: Quest
    get() = quest {
        id = 3
        title = "新生降落"
        description =
            """
            水球軟體學院中主要有三個常常用來聊天和交流的頻道（話題閒聊/工程師生活/職涯攻略），讓我來帶著你慢慢融入大家吧～
            
            首先，${wsa.discussionAreaChannelId.toLink()} 是學院中最「閒」的頻道，紳士們在這個頻道中大聊軟體時事、八卦和各式各樣的科技話題。
            你可以在這裡分享任何你有興趣的議題，不用擔心自己是否話太多，或是怕自己想法不夠深，其他紳士夥伴很樂意與你談天說地的！
            
            來試試看吧！到話題閒聊區中留言：「`大家好，我是剛降落的 <暱稱>，請大家多多指教！`」
            大家都會和你打招呼的喲～
            
            $missionMessage
            
            請複製下方文字範本，到話題閒聊區和大家交流吧！
            ```
            大家好，我是剛降落的<暱稱>，請大家多多指教！
            ```  
            """.trimIndent()

        preCondition = QuestIdPreCondition(2)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.discussionAreaChannelId))

        nextQuest = SendContainsImageMessageInEngineerLifeChannelQuest

    }

private fun getNewbieConversationRegex(): RegexRule =
    """(大家好，我是剛降落的)(.+)(，請大家多多指教！)""".toRegexRule()

val Quests.SendContainsImageMessageInEngineerLifeChannelQuest: Quest
    get() = quest {
        id = 4
        title = "融入大家"
        description =
            """
            接著，我要帶你前往非常好融入的 ${wsa.engineerLifeChannelId.toLink()} 頻道，工程師紳士們會在這裡分享和「軟體」全然無關的生活話題。

            不管是減肥方針、運動、美食分享、上班日常或下班生活、Work-Life Balance、旅遊日誌，都能在此分享！
            融入大家最簡單的方式，就是分享一張自己的美食照、三餐照、生活照⋯⋯只要能體現你日常生活的圖片都好。
            
            上班辛苦了，在生活層面上，我們也要好好享受才行。
            
            $missionMessage
            """.trimIndent()

        preCondition = QuestIdPreCondition(3)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        criteria = MessageSentCriteria(ChannelIdRule(wsa.engineerLifeChannelId), hasImageRule = BooleanRule.TRUE)

        nextQuest = ReplyToAnyoneInCareerAdvancementTopicChannelQuest
    }

val Quests.ReplyToAnyoneInCareerAdvancementTopicChannelQuest: Quest
    get() = quest {
        id = 5
        title = "職涯攻略"
        description =
            """          
        最後，是充滿含金量和高談闊論的 ${wsa.careerAdvancementTopicChannelId.toLink()}。
        
        由於學院中有許多在國內外高就的紳士夥伴，通常只要你願意請益大家，大家都會回覆你。
        學院提倡的是「藉由輸出來內化自己的思路」的費曼學習方法，所以其實在職涯攻略區，即便大家不斷地聊同一個話題，我們也會想要不斷從話題中，藉由打字交流來去提煉新的智慧，不必害羞也不必客氣。
        
        所以來試試看吧！試著和大家分享一下自己眼下遇到的「職涯煩惱」。
        如果你沒有煩惱的話，也能夠簡單地在頻道中回覆某則訊息，給予他人建議、或是最簡單的給予他人稱讚或認可。
        
        $missionMessage
            """.trimIndent()

        preCondition = QuestIdPreCondition(4)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f,
        )

        criteria =
            MessageSentCriteria(ChannelIdRule(wsa.careerAdvancementTopicChannelId), hasRepliedRule = BooleanRule.TRUE)

        nextQuest = watchVideoQuest
    }

val Quests.watchVideoQuest: Quest
    get() = quest {
        id = 6
        title = "學院精華影片"
        description = """       
            在學會如何自在地和大家聊天交流和參與話題之後，接下來要來帶你好好逛一下這個學院。

            我認為：「一個好的社群，會留下大家的足跡，這樣的社群就像是一座觀光勝地，逛都逛不完。」
            
            水球軟體學院也是以這個為願景去打造的，院長非常認真地帶領社群幹部打造了各種線上聚會和節目，所以如果你在加入學院之後，感覺「自己突然變得好充實啊！！」是一件非常正常的事情，千萬不要客氣 ^^
            
            這個任務非常簡單，請你在 ${wsa.featuredVideosChannelId.toLink()} 論壇中，找一部精華影片來看，並在留言區留下你的觀影心得，或是任何一種支持或想法都可以喔！       
        """.trimIndent()

        reward = Reward(
            100u,
            100u,
            1.0f,
        )

        preCondition = QuestIdPreCondition(5)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        criteria = MessageSentCriteria(ChannelIdRule(wsa.featuredVideosChannelId))

        nextQuest = flagPostQuest
    }

val Quests.flagPostQuest: Quest
    get() = quest {
        id = 7
        title = "全民插旗：把學院當成自己的家"
        description =
            """ 
            讓大家認識了你之後，還不夠！接下來我要教你如何「把學院當成自己的家！」
            在學院中，大家都會在 ${wsa.flagPostChannelId.toLink()} 論壇中，開「個人串」來記錄自己的各項心得或是日誌。
            
            這就是像在學院中，找一個小角角讓其他夥伴能關注你的動態，而由於論壇貼文的特性，你在自己個人串中的訊息，都只會推送給有訂閱你個人串的夥伴，不會吵到大家喔！
            
            所以大家很喜歡在自己的個人串中，更自在地貼一些固定的紀錄或日誌，好比運動記錄和喝水小卡，以及水球院長也有在 ${wsa.waterBallJournalPostId.toLink()} 分享他的創業日誌，或是在 ${wsa.waterBallLoseWeightPostId.toLink()} 分享他的減肥挑戰。
            
            如果你也有自己的創作想分享，也能夠和純函式的 Vincent 一樣 ${wsa.flagPostGuideId.toLink()} ，開一個串來固定分享自己的產品開發日誌，即便是業配也沒關係的，勇於分享軟體創作是一件好事！
            
            所以請你練習看看，先開一個屬於你的「個人串」吧，並且貼文的名稱要打上 `<你的暱稱>`。
            """.trimIndent() //TODO: 尚未將暱稱條件加入 criteria

        preCondition = QuestIdPreCondition(6)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f
        )
        criteria = PostCriteria(wsa.flagPostChannelId)

        nextQuest = SendMessageInVoiceChannelQuest
    }

val Quests.SendMessageInVoiceChannelQuest: Quest
    get() = quest {
        id = 8
        title = "到處吃瓜"
        description =
            """
            水球軟體學院的其中一項最受大家喜愛的文化，就是所謂的「吃瓜文化」啦！
            
            由於學院活動很多，還有各式各樣的社團和讀書會，你會時不時看見有人在「語音頻道中」開會、聚會或是閒聊。
            這時候就是咱們網路鄉民紳士的福音啦！！
            
            完全不要害羞，直接進去「吃瓜」吧！
            想吃瓜就吃瓜，完全不用經過該語音頻道「與會者」的同意的，「被吃瓜」是大家早就能預期的事了 XD
            
            很好玩吧！給你一個挑戰，加入「超過 2 人」的任意社團會議間語音頻道中，並在該語音頻道的訊息區發表 1 則訊息（可以和大家打招呼，或是問問大家在幹什麼）。            
            """.trimIndent()

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        preCondition = QuestIdPreCondition(7)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        //TODO 方便測試，先把需求人數改成 1 個人
        criteria = MessageSentCriteria(ChannelIdRule.ANY_CHANNEL, numberOfVoiceChannelMembersRule = AtLeastRule(1))

        nextQuest = JoinActivityQuest
    }

val Quests.JoinActivityQuest: Quest
    get() = quest {
        id = 9
        title = "任務:參加一場活動"
        description =
            """
            參與名稱為 test 的活動，並停留 10 秒
            """.trimIndent()

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        criteria = JoinActivityCriteria("test", 1, 10)
        nextQuest = quizQuest
    }

val Quests.quizQuest: Quest
    get() = quest {
        id = 10
        title = "考試"
        description =
            """
            恭喜你，你已經通過了一連串的新手試煉，接下來是最後一項「任務」，也就是「轉職任務」！
            只要做完這最後一項新手任務，你就能獲得「學院一轉紳士」的身份組！
            
            這最後一項任務，是你要通過一場小小的考試，在這一場考試中，我要來簡單地考考你水球軟體學院的願景和文化。你可以自由翻閱學院 ${wsa.wsaGuideLineChannelId.toLink()} 論壇中的文章，可以先從置頂文開始閱讀，然後從中尋找答案，來通過考試！
            
            考試並不難，理解水球軟體學院的願景和文化，也會讓你更能聽得懂社群中的一些「內梗」和幽默喔！
            趕緊試試看吧！
            """.trimIndent()

        preCondition = QuestIdPreCondition(8)

        roleType = RoleType.WSA_MEMBER

        periodType = PeriodType.MAIN_QUEST

        reward = Reward(
            100u,
            100u,
            1.0f
        )

        criteria = QuizCriteria("紳士考題", 60)

    }
