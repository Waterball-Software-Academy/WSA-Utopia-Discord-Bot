package tw.waterballsa.utopia.utopiagamification.weeklymission.ut

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.JoinVoiceChannelAction
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.JoinVoiceChannelMission
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.WeeklyMission
import java.time.Duration
import java.time.Instant
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
        val joinTime: Instant = Instant.now().minus(Duration.ofMinutes(30))
        val voiceChannelId = "學院閃電秀"
        val joinVoiceChannelMission = JoinVoiceChannelMission(
                gentlemanA.id,
                voiceChannelId,
                leastHeadCount = 47,
                timeRange = 25,
                joinTime
        )
        // When
        val leaveVoiceChannel = gentlemanA.leaveVoiceChannel(voiceChannelId, 50)
        joinVoiceChannelMission.progress(leaveVoiceChannel)
        // Then
        val expectedExp = 5260u
        assertThat(gentlemanA.exp).isEqualTo(expectedExp.toULong())
        assertThat(joinVoiceChannelMission.status).isEqualTo(WeeklyMission.Status.COMPLETE)
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
        val startTime: Instant = Instant.now().minus(Duration.ofMinutes(5))
        val joinVoiceChannelMission = JoinVoiceChannelMission(
                gentlemanA.id,
                "學院閃電秀",
                leastHeadCount = 47,
                timeRange = 25,
                startTime
        )
        // When
        val leaveVoiceChannel = gentlemanA.leaveVoiceChannel("學院閃電秀", 50)
        leaveVoiceChannel.progress(joinVoiceChannelMission)
        // Then
        val expectedExp = 5000u
        assertThat(gentlemanA.exp).isEqualTo(expectedExp.toULong())
        assertThat(joinVoiceChannelMission.status).isEqualTo(WeeklyMission.Status.PROGRESS)
    }

    private fun Player.leaveVoiceChannel(voiceChannelId: String, accumulator: Int) = JoinVoiceChannelAction(this, voiceChannelId, accumulator)

}
