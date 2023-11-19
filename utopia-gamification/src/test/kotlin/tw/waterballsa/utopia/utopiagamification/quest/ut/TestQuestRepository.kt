package tw.waterballsa.utopia.utopiagamification.quest.ut

import tw.waterballsa.utopia.utopiagamification.quest.domain.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.toRegexRule
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository

class TestQuestRepository : QuestRepository {
    private val quests = mutableListOf<Quest>(
        Quest(
            id = 1,
            title = "解鎖學院",
            description = "",
            preCondition = EmptyPreCondition(),
            roleType = RoleType.EVERYONE,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f, RoleType.WSA_MEMBER),
            criteria = MessageReactionCriteria(
                ChannelIdRule("unlockEntryChannelId"),
                "unlockEntryMessageId",
                "🔑"
            )
        ),
        Quest(
            id = 2,
            title = "自我介紹",
            description = "",
            reward = Reward(100u, 100u, 1.0f),
            preCondition = QuestIdPreCondition(1),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            criteria = MessageSentCriteria(
                ChannelIdRule("selfIntroChannelId"),
                regexRule = """【(.|\n)*】(.|\n)*工作職位：?(.|\n)*((公司產業：?(:)?(.|\n)*))?專長：?(.|\n)*興趣：?(.|\n)*簡介：?.(.|\n)*((三件關於我的事，猜猜哪一件是假的：?(:)?(.|\n)*))?""".toRegexRule()
            )
        ),
        Quest(
            id = 3,
            title = "新生降落",
            description = "",
            preCondition = QuestIdPreCondition(2),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria = MessageSentCriteria(ChannelIdRule("discussionAreaChannelId"))
        ),
        Quest(
            id = 4,
            title = "融入大家",
            description = "這是一個PO照片任務",
            preCondition = QuestIdPreCondition(3),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria = MessageSentCriteria(
                ChannelIdRule("engineerLifeChannelId"),
                hasImageRule = BooleanRule.TRUE
            )
        ),
        Quest(
            id = 5,
            title = "職涯攻略",
            description = "",
            preCondition = QuestIdPreCondition(4),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria =
            MessageSentCriteria(
                ChannelIdRule("careerAdvancementTopicChannelId"),
                hasRepliedRule = BooleanRule.TRUE
            )
        ),
        Quest(
            id = 6,
            title = "學院精華影片",
            description = "",
            preCondition = QuestIdPreCondition(5),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria = MessageSentCriteria(ChannelIdRule("featuredVideosChannelId"))
        ),
        Quest(
            id = 7,
            title = "全民插旗：把學院當成自己的家",
            description = "",
            preCondition = QuestIdPreCondition(6),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria = PostCriteria(ChannelIdRule("flagPostChannelId"))
        ),
        Quest(
            id = 8,
            title = "到處吃瓜",
            description = "",
            reward = Reward(100u, 100u, 1.0f),
            preCondition = QuestIdPreCondition(7),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            criteria = MessageSentCriteria(
                ChannelIdRule.ANY_CHANNEL,
                numberOfVoiceChannelMembersRule = AtLeastRule(2)
            )
        ),
        Quest(
            id = 9,
            title = "參與院長主持的學院節目",
            description = "",
            reward = Reward(100u, 100u, 1.0f),
            criteria = JoinActivityCriteria("遊戲微服務計畫：水球實況", 1, 5)
        ),
        Quest(
            id = 10,
            title = "考試",
            description = "",
            preCondition = QuestIdPreCondition(8),
            roleType = RoleType.WSA_MEMBER,
            periodType = PeriodType.MAIN_QUEST,
            reward = Reward(100u, 100u, 1.0f),
            criteria = QuizCriteria("紳士考題", 4, 5),
        ),
    )

    override fun findById(id: Int): Quest? {
        return quests.find { it.id == id }
    }

    override fun save(quest: Quest): Quest {
        quests.add(quest)
        return quest
    }
}


