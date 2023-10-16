package tw.waterballsa.utopia.utopiagmification.quest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.*
import tw.waterballsa.utopia.utopiagamification.activity.domain.Activity
import tw.waterballsa.utopia.utopiagamification.activity.domain.Activity.State.ACTIVE
import tw.waterballsa.utopia.utopiagamification.quest.domain.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.actions.JoinActivityCriteria
import java.util.*
import java.util.UUID.randomUUID

class UtopiaGamificationQuestTest {

    private lateinit var playerA: Player
    private lateinit var quest: Quest
    private lateinit var activity: Activity

    @BeforeEach
    fun setup() {
        playerA = Player(id = randomUUID().toString(), name = "A")
        quest = Quest(
            id = 9,
            title = "參與院長主持的學院節目",
            description = "",
            reward = Reward(100u, 100u, 1.0f),
            criteria = JoinActivityCriteria("遊戲微服務計畫：水球實況", 1, 0)
        )
        activity = Activity(
            "遊戲微服務計畫：水球實況",
            "hostId",
            "遊戲微服務計畫：水球實況",
            "anyChannelId",
            ACTIVE
        )
    }

    @TestTemplate
    @ExtendWith(MissionTestInvocationContextProvider::class)
    fun `test player accept mission`(missionTestCase: MissionTestCase) {
        with(missionTestCase) {
            val mission = player.acceptQuest(quest)

            assertEquals(isMatchAction, mission.match(action))
            mission.carryOut(action)

            assertEquals(isMissionCompleted, mission.isCompleted())
        }
    }

    @Test
    fun `given player accept mission and join an activity, when player stay 0 minute, then mission should be completed`() {
        val activityMission = playerA.acceptQuest(quest)
        activity.join(playerA)

        val leaveActivityAction = activity.leave(playerA)!!
        activityMission.carryOut(leaveActivityAction)

        assertEquals(playerA.id, leaveActivityAction.player.id)
        assertEquals(activity.eventName, leaveActivityAction.eventName)
        assertTrue(activityMission.isCompleted())
    }


    @Test
    fun `given player accept mission, when player didn't join activity, then mission should be failed`() {
        val activityMission = playerA.acceptQuest(quest)

        val leaveActivityAction = activity.leave(playerA)

        assertNull(leaveActivityAction)
        assertFalse(activityMission.isCompleted())
    }

    private fun Player.acceptQuest(quest: Quest) = Mission(this, quest)
}
