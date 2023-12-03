package tw.waterballsa.utopia.utopiagamification.weeklymission.ut

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.SendMessageAction
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.SendMessageMission
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.WeeklyMission
import java.util.*

class WeeklyMissionUnitTest {

    private lateinit var gentlemanA: Player

    @BeforeEach
    fun setup() {
        gentlemanA = Player(id = UUID.randomUUID().toString(), name = "A", exp = 5000uL)
    }

    @Test
    @DisplayName(
        """
        Case 1：紳士完成「發佈訊息每週任務」
            - Given：紳士當前狀態並接受到發佈訊息任務
                紳士 ：A，目前有 5000 經驗值
                發布訊息任務：
                    - 話題閒聊區
                    - (要)附圖片
                    - (不用)要標記人
                    - 字數限制為 20 字
                    - 發佈 1 則數量
                    - 獎勵經驗值 130 EXP
            - When：紳士做了
                紳士 A：在話題閒聊區，發佈包含圖片的 20 字訊息
            - Then：紳士（完成|未完成），當下經驗值為
                紳士 A：完成任務，目前有 5130 經驗值
    """
    )
    fun `gentleman completed the send message weekly mission`() {
        // Given
        val sendMessageMission = SendMessageMission(
            gentlemanA.id,
            "話題閒聊區",
            hasImage = true,
            isTag = false,
            wordLength = 20,
            publishedCount = 1,
            progressCount = 0,
        )
        val sendMessageAction = gentlemanA.sendMessage("話題閒聊區", hasImage = true, isTag = false, content = "t".repeat(20))
        sendMessageAction.progress(sendMessageMission)

        Assertions.assertThat(gentlemanA.exp).isEqualTo(5130)
        Assertions.assertThat(sendMessageMission.status).isEqualTo(WeeklyMission.Status.COMPLETE)
    }

    @Test
    @DisplayName(
        """
        Case 2：紳士未完成「發佈訊息每週任務」
            - Given：紳士當前狀態並接受到發佈訊息任務
                紳士 A，目前有 5000 經驗值
                發布訊息任務：
                - 話題閒聊區
                - (不用)附圖片
                - (要)標記人
                - 字數限制為 100 字
                - 發佈 1 則數量
                - 獎勵經驗值 180 EXP
            - When：紳士做了
                紳士 A：在話題閒聊區，發佈包含圖片的 20 字訊息
            - Then：紳士（完成|未完成），當下經驗值為
                紳士 A：未完成任務，目前有 5000 經驗值
    """
    )
    fun `gentleman hasn't complete the send message weekly mission`() {
        val sendMessageMission = SendMessageMission(
                gentlemanA.id,
                "話題閒聊區",
                hasImage = false,
                isTag = true,
                wordLength = 100,
                publishedCount = 1,
                progressCount = 0,
        )

        val sendMessageAction = gentlemanA.sendMessage("話題閒聊區", hasImage = true, isTag = false, content = "t".repeat(20))
        sendMessageAction.progress(sendMessageMission)

        Assertions.assertThat(gentlemanA.exp).isEqualTo(5130)
        Assertions.assertThat(sendMessageMission.status).isEqualTo(WeeklyMission.Status.COMPLETE)
    }

    
    private fun Player.sendMessage(channelId: String, hasImage: Boolean, isTag: Boolean,content: String) : SendMessageAction
        = SendMessageAction(channelId, hasImage, isTag, content)
    
}
