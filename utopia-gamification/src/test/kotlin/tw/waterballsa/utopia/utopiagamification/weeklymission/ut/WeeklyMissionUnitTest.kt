package tw.waterballsa.utopia.utopiagamification.weeklymission.ut

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.SendMessageMission
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
        var sendMessageMission = SendMessageMission(
            gentlemanA.id,
            "話題閒聊區",
            hasImage = true,
            isTag = false,
            wordLength = 20,
            publishedCount = 1
        )


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

    }

    @Test
    @DisplayName(
        """
        Case 1：紳士完成「加入語音頻道每週任務」
        - Given：紳士當前狀態並接受到加入語音頻道任務
            紳士 A，目前有 5000 經驗值
            加入語音頻道任務：
                - 累積達到 47 人（200 EXP）
                - 待 25 分鐘（60 EXP）
                - 獎勵經驗值為 260 EXP
        - When：紳士做了
            紳士 A：在學院閃電秀區，期間最高人數達到 50 人，待了 30 分鐘
        - Then：紳士（完成|未完成），當下經驗值為
            紳士 A：完成任務，目前有經驗值為 5260
    """
    )
    fun `gentleman completed the join voice channel weekly mission`() {

    }

    @Test
    @DisplayName(
        """
        Case 2：紳士未完成「加入語音頻道每週任務」
        - Given：紳士當前狀態並接受到加入語音頻道任務
            紳士 A，目前有 5000 經驗值
            加入語音頻道任務：
                - 累積達到 47 人（200 EXP）
                - 待 25 分鐘（60 EXP）
                - 獎勵經驗值為 260 EXP
        - When：紳士做了
            紳士 A：在學院閃電秀區，期間最高人數達到 50 人，待了 5 分鐘
        - Then：紳士（完成|未完成），當下經驗值為
            紳士 A：未完成任務，目前有經驗值為 5000
    """
    )
    fun `gentleman hasn't completed the join voice channel weekly mission`() {

    }
}
