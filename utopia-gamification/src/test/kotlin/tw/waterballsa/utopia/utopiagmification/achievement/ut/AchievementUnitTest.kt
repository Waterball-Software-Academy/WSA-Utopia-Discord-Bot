package tw.waterballsa.utopia.utopiagmification.achievement.ut

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Name
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Rule
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.LongArticleAchievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.TopicMasterAchievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.SendMessageAction
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType.LONG_ARTICLE
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType.TOPIC_MASTER
import java.util.UUID.randomUUID

class AchievementUnitTest {

    private lateinit var playerA: Player

    private lateinit var progression: Achievement.Progression

    private val longArticleAchievement = LongArticleAchievement(
        LongArticleAchievement.Condition(800),
        Rule(LONG_ARTICLE, 1),
        Reward(1000u, LONG_ARTICLE)
    )

    private val topicMasterAchievement = TopicMasterAchievement(
        TopicMasterAchievement.Condition(),
        Rule(TOPIC_MASTER, 300),
        Reward(2500u, TOPIC_MASTER)
    )

    @BeforeEach
    fun setup() {
        playerA = Player(
            id = randomUUID().toString(),
            name = "A",
            exp = 1200uL,
            jdaRoles = mutableListOf()
        )

        progression = Achievement.Progression(
            randomUUID().toString(),
            playerId = playerA.id,
            name = Name.TOPIC_MASTER,
            type = TEXT_MESSAGE,
            count = 0
        )
    }

    @Test
    @DisplayName(
        """
        訊息太短，無法觸發長文
            Given：
                - 玩家 A 沒有「長文成就」身份組
                - 玩家 A 的 EXP = 1200
            When：
                - 玩家 A 在文字頻道，或是某個論壇輸入訊息 “Test123456”
            Then：
                - 玩家 A 仍然沒有「長文成就」身份組
                - 玩家 A 的 EXP = 1200
    """
    )
    fun `player doesn't achieve the article-achievement`() {

        // when
        val sendMessageAction = playerA.sendMessage("Test123456")
        val progression = sendMessageAction.progress(longArticleAchievement, progression)
        sendMessageAction.achieve(longArticleAchievement, progression)

        // then
        assertEquals(playerA.exp, 1200uL)
        assertFalse(playerA.hasRole(LONG_ARTICLE.description))
    }


    @Test
    @DisplayName(
        """
        發布超過 800 的字的長文
            玩家 A 發表了一篇長文
            Given：
                - 玩家 A 沒有「長文成就」身份組
                - 玩家 A 在文字頻道，或是某個論壇發表了一篇字數大於 800 字的貼文
                - 玩家 A EXP = 1200
            When：
                - 玩家 A 發佈文章
            Then：
                - 玩家 A 獲得「長文成就」身份組，EXP = 2200
    """
    )
    fun `player achieve the article-achievement`() {
        val article = "1".repeat(1000)

        //when
        val sendMessageAction = playerA.sendMessage(article)
        //then
        val progression = sendMessageAction.progress(longArticleAchievement, progression)
        sendMessageAction.achieve(longArticleAchievement, progression)

        assertEquals(playerA.exp, 2200uL)
        assertTrue(playerA.hasRole(LONG_ARTICLE.name))
    }

    @Test
    @DisplayName(
        """
        留言數過少，無法得到話題高手
            Given：
                - 玩家 A 沒有「話題高手」身份組
                - 玩家 A EXP = 1200
            When：
                - 玩家 A 發佈 1 則留言
            Then：
                - 玩家 A 沒有「話題高手」身份組
                - 玩家 A EXP = 1200
    """
    )
    fun `player doesn't achieve the topic-master-achievement`() {
        // when
        val sendMessageAction = playerA.sendMessage("一二三四五")
        val progression = sendMessageAction.progress(topicMasterAchievement, progression)
        sendMessageAction.achieve(topicMasterAchievement, progression)

        // then
        assertFalse(playerA.hasRole(TOPIC_MASTER.description))
        assertEquals(playerA.exp, 1200uL)
    }

    @Test
    @DisplayName(
        """
        超過三百則，獲得話題高手
            Given：
                - 玩家 A 已經發言過 299 則訊息
                - 玩家 A 沒有「話題高手」身份組
                - 玩家 A EXP = 1200
            When：當玩家 A 再送出一則訊息「ABC」
            Then：
                - 玩家 A 獲得「話題高手」成就的訊息
                - 玩家 A EXP = 3700
    """
    )
    fun `player achieve the topic-master-achievement`() {
        val sendTwoHundredNinetyNineMessageProgression = Achievement.Progression(
            randomUUID().toString(),
            playerId = playerA.id,
            name = Name.TOPIC_MASTER,
            type = TEXT_MESSAGE,
            count = 299
        )
        // when
        val sendMessageAction = playerA.sendMessage("一二三四五")
        val progression =
            sendMessageAction.progress(topicMasterAchievement, sendTwoHundredNinetyNineMessageProgression)
        sendMessageAction.achieve(topicMasterAchievement, progression)

        // then
        assertTrue(playerA.hasRole(TOPIC_MASTER.name))
        assertEquals(playerA.exp, 3700uL)
    }

    private fun Player.sendMessage(words: String): SendMessageAction =
        SendMessageAction(this, words)
}
