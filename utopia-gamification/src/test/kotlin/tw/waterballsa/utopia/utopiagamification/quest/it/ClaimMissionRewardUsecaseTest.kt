package tw.waterballsa.utopia.utopiagamification.quest.it

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import tw.waterballsa.utopia.utopiagamification.quest.domain.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.State.CLAIMED
import tw.waterballsa.utopia.utopiagamification.quest.domain.State.COMPLETED
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.ClaimInProgressMissionException
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.ClaimedMissionException
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.ClaimMissionRewardPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.ClaimMissionRewardUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest

@UtopiaTest
class ClaimMissionRewardUsecaseTest @Autowired constructor(
    private val missionRepository: MissionRepository,
    private val playerRepository: PlayerRepository,
    private val claimMissionRewardUsecase: ClaimMissionRewardUsecase
) {

    private val playerA = Player("A", "A")
    private val quest = Quest(
        id = 1,
        title = "",
        description = "",
        criteria = TestCriteria(),
        reward = Reward(
            100u,
            100u,
            1.0f
        )
    )

    @BeforeEach
    fun setup() {
        playerRepository.savePlayer(playerA)
    }

    @Test
    fun `given completed mission, when claim reward to playerA, then playerA gain exp, and mission is claimed`() {
        //given
        val completedMission = Mission(playerA, quest, COMPLETED)
        missionRepository.saveMission(completedMission)

        val request = ClaimMissionRewardUsecase.Request(playerA.id, quest.id)
        val presenter = ClaimMissionRewardPresenter()

        //when
        claimMissionRewardUsecase.execute(request, presenter)

        //then
        val player = playerRepository.findPlayerById(playerA.id)
        val mission = missionRepository.findPlayerMissionByQuestId(playerA.id, quest.id)

        assertThat(player).isNotNull
        assertThat(player?.exp).isEqualTo(quest.reward.exp)
        assertThat(player?.level).isEqualTo(2u)

        assertThat(mission).isNotNull
        assertThat(mission?.state).isEqualTo(CLAIMED)
    }

    @Test
    fun `when claim in-progress mission, then should be fail`() {
        val mission = Mission(playerA, quest)
        missionRepository.saveMission(mission)

        val request = ClaimMissionRewardUsecase.Request(playerA.id, quest.id)
        val presenter = ClaimMissionRewardPresenter()

        assertThatExceptionOfType(ClaimInProgressMissionException::class.java).isThrownBy {
            claimMissionRewardUsecase.execute(request, presenter)
        }

    }

    @Test
    fun `when claim claimed mission, then should be fail`() {
        val mission = Mission(playerA, quest, CLAIMED)
        missionRepository.saveMission(mission)

        val request = ClaimMissionRewardUsecase.Request(playerA.id, quest.id)
        val presenter = ClaimMissionRewardPresenter()

        assertThatExceptionOfType(ClaimedMissionException::class.java).isThrownBy {
            claimMissionRewardUsecase.execute(request, presenter)
        }
    }
}

class TestCriteria : Action.Criteria() {
    override fun meet(action: Action): Boolean = true
}
