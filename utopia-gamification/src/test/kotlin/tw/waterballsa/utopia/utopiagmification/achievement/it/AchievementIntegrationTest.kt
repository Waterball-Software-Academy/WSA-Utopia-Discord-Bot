package tw.waterballsa.utopia.utopiagmification.achievement.it

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.ProgressionRepository
import tw.waterballsa.utopia.utopiagamification.achievement.application.usecase.ProgressAchievementUseCase
import tw.waterballsa.utopia.utopiagamification.achievement.application.usecase.ProgressAchievementUseCase.Request
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Progression
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.presenter.ProgressAchievementPresenter
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType.*
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest
import java.util.UUID.randomUUID

@UtopiaTest
class AchievementIntegrationTest @Autowired constructor(
    private val progressAchievementUsecase: ProgressAchievementUseCase,
    private val playerRepository: PlayerRepository,
    private val progressionRepository: ProgressionRepository
) {
    private lateinit var playerA: Player


    @BeforeEach
    fun setup() {
        playerA = playerRepository.savePlayer(Player(id = randomUUID().toString(), name = "A", exp = 1200uL))
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
        val request = Request(playerA.id, TEXT_MESSAGE, "Test123456")
        val presenter = ProgressAchievementPresenter()
        progressAchievementUsecase.execute(request, presenter)

        // then
        val player = playerRepository.findPlayerById(playerA.id)
        assertThat(player).isNotNull
        assertThat(player!!.exp).isEqualTo(1200uL)
        assertThat(player.hasRole(LONG_ARTICLE)).isFalse
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
        //when
        val article = "1".repeat(1000)
        val request = Request(playerA.id, TEXT_MESSAGE, article)
        val presenter = ProgressAchievementPresenter()
        progressAchievementUsecase.execute(request, presenter)

        //then
        val player = playerRepository.findPlayerById(playerA.id)
        assertThat(player).isNotNull
        assertThat(player!!.exp).isEqualTo(2200uL)
        assertThat(player.hasRole(LONG_ARTICLE)).isTrue
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
        val request = Request(playerA.id, TEXT_MESSAGE, "一二三四五")
        val presenter = ProgressAchievementPresenter()
        progressAchievementUsecase.execute(request, presenter)

        // then
        val player = playerRepository.findPlayerById(playerA.id)
        assertThat(player).isNotNull
        assertThat(player!!.exp).isEqualTo(1200uL)
        assertThat(player.hasRole(TOPIC_MASTER)).isFalse
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
        // given
        playerA.sendMessages(299)

        // when
        val request = Request(playerA.id, TEXT_MESSAGE, "一二三四五")
        val presenter = ProgressAchievementPresenter()
        progressAchievementUsecase.execute(request, presenter)

        // then
        val player = playerRepository.findPlayerById(playerA.id)
        assertThat(player).isNotNull
        assertThat(player!!.exp).isEqualTo(3700uL)
        assertThat(player.hasRole(TOPIC_MASTER)).isTrue
    }

    @Test
    @DisplayName(
        """
        超過三百則，獲得話題高手，並且發布超過 800 的字的長文
            Given：
                玩家 A 已經發言過 299 則訊息
                玩家 A 沒有「話題高手」身份組
                玩家 A 沒有「長文成就」身份組
                玩家 A EXP = 1200
            When：當玩家 A 再送出一篇字數大於 800 字的貼文
            Then：
                玩家 A 獲得「話題高手」成就的訊息
                玩家 A 獲得「長文成就」身份組
                玩家 A EXP = 4700
    """
    )
    fun `player achieve the topic-master-achievement and article-achievement`() {
        // given
        playerA.sendMessages(299)

        // when
        val request = Request(playerA.id, TEXT_MESSAGE, "1".repeat(801))
        val presenter = ProgressAchievementPresenter()
        progressAchievementUsecase.execute(request, presenter)

        // then
        val player = playerRepository.findPlayerById(playerA.id)
        assertThat(player).isNotNull
        assertThat(player!!.exp).isEqualTo(4700uL)
        assertThat(player.hasRole(TOPIC_MASTER)).isTrue
        assertThat(player.hasRole(LONG_ARTICLE)).isTrue
    }

    private fun Player.sendMessages(messageCount: Int) {
        val sendMessagesProgression = Progression(
                randomUUID().toString(),
                playerId = id,
                name = Achievement.Name.TOPIC_MASTER,
                type = TEXT_MESSAGE,
                count = messageCount)
        progressionRepository.save(sendMessagesProgression)
    }
}
