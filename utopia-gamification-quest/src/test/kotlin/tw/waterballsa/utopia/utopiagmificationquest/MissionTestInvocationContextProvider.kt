package tw.waterballsa.utopia.utopiagmificationquest

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import tw.waterballsa.utopia.utopiagamificationquest.domain.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.PeriodType.MAIN_QUEST
import tw.waterballsa.utopia.utopiagamificationquest.domain.RoleType.EVERYONE
import tw.waterballsa.utopia.utopiagamificationquest.domain.RoleType.WSA_MEMBER
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.BooleanRule.TRUE
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.ChannelIdRule.Companion.ANY_CHANNEL
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.toRegexRule
import java.util.UUID.randomUUID
import java.util.stream.Stream

class MissionTestInvocationContextProvider : TestTemplateInvocationContextProvider {

    private val player: Player = Player(id = randomUUID().toString(), name = "A")

    override fun supportsTestTemplate(context: ExtensionContext?): Boolean = true

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext?): Stream<TestTemplateInvocationContext> =
        Stream.of(
            MissionTestCase(
                "given player accept mission '考試', when player was act 5 correct quizzes, then mission should be completed",
                player,
                quest {
                    id = 10
                    title = "考試"
                    description = ""
                    preCondition = QuestIdPreCondition(8)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = QuizCriteria("紳士考題", 4, 5)
                },
                QuizAction(player, "紳士考題", 5),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '考試', when player was act 0 correct quiz, then mission should be failed",
                player,
                quest {
                    id = 10
                    title = "考試"
                    description = ""
                    preCondition = QuestIdPreCondition(8)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = QuizCriteria("紳士考題", 4, 5)
                },
                QuizAction(player, "紳士考題", 0),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '到處吃瓜', when player sent a message in two people channel, then mission should be completed",
                player,
                quest {
                    id = 8
                    title = "到處吃瓜"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(7)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    criteria = MessageSentCriteria(
                        ANY_CHANNEL,
                        numberOfVoiceChannelMembersRule = AtLeastRule(2)
                    )
                },
                MessageSentAction(
                    player,
                    "eatWatermelonEveryWhere",
                    "hello watermelon",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 2
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '到處吃瓜', when player sent a message in zero person channel, then mission should be failed",
                player,
                quest {
                    id = 8
                    title = "到處吃瓜"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(7)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    criteria = MessageSentCriteria(
                        ANY_CHANNEL,
                        numberOfVoiceChannelMembersRule = AtLeastRule(2)
                    )
                },
                MessageSentAction(
                    player,
                    "test",
                    "",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept flagPost mission, when player post a message in right channel, then mission should be completed",
                player,
                quest {
                    id = 7
                    title = "全民插旗：把學院當成自己的家"
                    description = ""
                    preCondition = QuestIdPreCondition(6)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = PostCriteria(ChannelIdRule("flagPostChannelId"))
                },
                PostAction(
                    player,
                    "flagPostChannelId",
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept flagPost mission, when player post a message in wrong channel, then mission should be failed",
                player,
                quest {
                    id = 7
                    title = "全民插旗：把學院當成自己的家"
                    description = ""
                    preCondition = QuestIdPreCondition(6)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = PostCriteria(ChannelIdRule("flagPostChannelId"))
                },
                PostAction(
                    player,
                    "featuredVideosChannelId",
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '學院精華影片', when player send a message in right channel, then mission should be completed",
                player,
                quest {
                    id = 6
                    title = "學院精華影片"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(5)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(ChannelIdRule("featuredVideosChannelId"))
                },
                MessageSentAction(
                    player,
                    "featuredVideosChannelId",
                    "good video",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '學院精華影片', when player send a message in wrong channel, then mission should be failed",
                player,
                quest {
                    id = 6
                    title = "學院精華影片"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(5)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(ChannelIdRule("featuredVideosChannelId"))
                },
                MessageSentAction(
                    player,
                    "flagPostChannelId",
                    "good video",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '職涯攻略', when player reply a message in right channel, then mission should be completed",
                player,
                quest {
                    id = 5
                    title = "職涯攻略"
                    description = ""
                    preCondition = QuestIdPreCondition(4)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(
                        ChannelIdRule("careerAdvancementTopicChannelId"),
                        hasRepliedRule = TRUE
                    )
                },
                MessageSentAction(
                    player,
                    "careerAdvancementTopicChannelId",
                    "I want to go to good company",
                    hasReplied = true,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '職涯攻略', when player send a message in right channel, then mission should be failed",
                player,
                quest {
                    id = 5
                    title = "職涯攻略"
                    description = ""
                    preCondition = QuestIdPreCondition(4)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria =
                        MessageSentCriteria(
                            ChannelIdRule("careerAdvancementTopicChannelId"),
                            hasRepliedRule = TRUE
                        )
                },
                MessageSentAction(
                    player,
                    "careerAdvancementTopicChannelId",
                    "I want to go to good company",
                    hasReplied = false,
                    hasImage = true,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '融入大家', when player send a image in correct channel, then mission should be completed",
                player,
                quest {

                    id = 4
                    title = "融入大家"
                    description = "這是一個PO照片任務"
                    preCondition = QuestIdPreCondition(3)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(
                        ChannelIdRule("engineerLifeChannelId"),
                        hasImageRule = TRUE
                    )
                },
                MessageSentAction(
                    player,
                    "engineerLifeChannelId",
                    "I believe I can fly",
                    hasReplied = false,
                    hasImage = true,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '融入大家', when player send a plain text in correct channel, then mission should be failed",
                player,
                quest {
                    id = 4
                    title = "融入大家"
                    description = "這是一個PO照片任務"
                    preCondition = QuestIdPreCondition(3)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(
                        ChannelIdRule("engineerLifeChannelId"),
                        hasImageRule = TRUE
                    )
                },
                MessageSentAction(
                    player,
                    "engineerLifeChannelId",
                    "I believe I can fly",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '新生降落', when player send a message in correct channel, then mission should be completed",
                player,
                quest {
                    id = 3
                    title = "新生降落"
                    description = ""
                    preCondition = QuestIdPreCondition(2)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(ChannelIdRule("discussionAreaChannelId"))
                },
                MessageSentAction(
                    player,
                    "discussionAreaChannelId",
                    "I am waterBall bot",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '新生降落', when player send an image in wrong channel, then mission should be failed",
                player,
                quest {
                    id = 3
                    title = "新生降落"
                    description = ""
                    preCondition = QuestIdPreCondition(2)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    reward = Reward(100u, 100u, 1.0f)
                    criteria = MessageSentCriteria(ChannelIdRule("discussionAreaChannelId"))
                },
                MessageSentAction(
                    player,
                    "careerAdvancementTopicChannelId",
                    "I am waterBall bot",
                    hasReplied = true,
                    hasImage = true,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '自我介紹', when player sent a message with wrong pattern, then mission should be failed",
                player,
                quest {
                    id = 2
                    title = "自我介紹"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(1)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    criteria = MessageSentCriteria(
                        ChannelIdRule("selfIntroChannelId"),
                        regexRule = """【(.|\n)*】(.|\n)*工作職位：?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(.|\n)*興趣：?(.|\n)*簡介：?.(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegexRule()
                    )
                }, MessageSentAction(
                    player,
                    "selfIntroChannelId",
                    """三件關於我的事，猜猜哪一件是假的：""",
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '自我介紹', when player sent a message with right pattern, then mission should be completed",
                player,
                quest {
                    id = 2
                    title = "自我介紹"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f)
                    preCondition = QuestIdPreCondition(1)
                    roleType = WSA_MEMBER
                    periodType = MAIN_QUEST
                    criteria = MessageSentCriteria(
                        ChannelIdRule("selfIntroChannelId"),
                        regexRule = """【(.|\n)*】(.|\n)*工作職位：?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(.|\n)*興趣：?(.|\n)*簡介：?.(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegexRule()
                    )
                }, MessageSentAction(
                    player, "selfIntroChannelId", """
                        【 playerA 】 
                        工作職位： <工作職位>
                        公司產業： <工作所在公司的產業類型>
                        專長： <專長>
                        興趣： <興趣>
                        簡介： <介紹一下你自己吧！>
                        
                        三件關於我的事，猜猜哪一件是假的：
                        1.
                        2.
                        3.""".trimIndent(),
                    hasReplied = false,
                    hasImage = false,
                    numberOfVoiceChannelMembers = 0
                ),
                isMatchAction = true,
                isMissionCompleted = true
            ),

            MissionTestCase(
                "given player accept mission '解鎖學院', when player react wrong emoji, then mission should be failed",
                player,
                quest {
                    id = 1
                    title = "解鎖學院"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f, WSA_MEMBER)
                    preCondition = EmptyPreCondition()
                    roleType = EVERYONE
                    periodType = MAIN_QUEST
                    criteria = MessageReactionCriteria(
                        ChannelIdRule("unlockEntryChannelId"),
                        "unlockEntryMessageId",
                        "🔑"
                    )
                },
                MessageReactionAction(
                    player,
                    "unlockEntryMessageId",
                    "🍒"
                ),
                isMatchAction = true,
                isMissionCompleted = false
            ),

            MissionTestCase(
                "given player accept mission '解鎖學院', when player react right emoji, then mission should be completed",
                player,
                quest {
                    id = 1
                    title = "解鎖學院"
                    description = ""
                    reward = Reward(100u, 100u, 1.0f, WSA_MEMBER)
                    preCondition = EmptyPreCondition()
                    roleType = EVERYONE
                    periodType = MAIN_QUEST
                    criteria = MessageReactionCriteria(
                        ChannelIdRule("unlockEntryChannelId"),
                        "unlockEntryMessageId",
                        "🔑"
                    )
                },
                MessageReactionAction(
                    player,
                    "unlockEntryMessageId",
                    "🔑"
                ),
                isMatchAction = true,
                isMissionCompleted = true
            )
        ).map { toInvocationContext(it) }

    private fun toInvocationContext(missionTestCase: MissionTestCase): TestTemplateInvocationContext {
        return object : TestTemplateInvocationContext {
            override fun getDisplayName(invocationIndex: Int): String = missionTestCase.displayName

            override fun getAdditionalExtensions(): List<Extension> = listOf(
                GenericTypedParameterResolver(missionTestCase)
            )
        }
    }
}
