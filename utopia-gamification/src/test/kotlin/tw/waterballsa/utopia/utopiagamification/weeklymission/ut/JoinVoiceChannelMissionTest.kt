package tw.waterballsa.utopia.utopiagamification.weeklymission.ut

import dev.minn.jda.ktx.messages.SendDefaults.content
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.JoinVoiceChannelMission
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.SendMessageAction
import java.util.*

class JoinVoiceChannelMissionTest {

    private lateinit var gentlemanA: Player

    @BeforeEach
    fun setup() {
        gentlemanA = Player(id = UUID.randomUUID().toString(), name = "A", exp = 5000uL)
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
        // Given
        var joinVoiceChannelMission = JoinVoiceChannelMission(
                gentlemanA.id,
                "學院閃電秀",
                leastHeadCount = 47,
                timeRange = 25
        )
        // When

        // Then
        assertThat(gentlemanA.exp).isEqualTo(5260)
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
        // Given
        var joinVoiceChannelMission = JoinVoiceChannelMission(
                gentlemanA.id,
                "學院閃電秀",
                leastHeadCount = 47,
                timeRange = 25
        )
        // When

        // Then
        assertThat(gentlemanA.exp).isEqualTo(5000)
    }

//    private fun Player.joinVoiceChannel(voiceChannelId: String, leastHeadCount: Int, timeRange: Int)
//            = SendMessageAction(channelId, hasImage, isTag, content)

}
