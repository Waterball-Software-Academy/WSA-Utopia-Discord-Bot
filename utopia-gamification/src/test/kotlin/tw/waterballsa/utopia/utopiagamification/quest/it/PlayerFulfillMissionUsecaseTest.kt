package tw.waterballsa.utopia.utopiagamification.quest.it

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.PlayerFulfillMissionPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.quest.ut.MissionTestCase
import tw.waterballsa.utopia.utopiagamification.quest.ut.MissionTestInvocationContextProvider
import tw.waterballsa.utopia.utopiagamification.quest.ut.TestQuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest

@Configuration
open class MockBeanConfig {

    @Primary
    @Bean
    open fun questRepository(): QuestRepository {
        return TestQuestRepository()
    }
}

@UtopiaTest
class PlayerFulfillMissionUsecaseTest @Autowired constructor(
    private val missionRepository: MissionRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase,
    private val playerRepository: PlayerRepository
) {

    @TestTemplate
    @ExtendWith(MissionTestInvocationContextProvider::class)
    fun `test player fulfill mission`(missionTestCase: MissionTestCase) {
        with(missionTestCase) {
            player.acceptQuest(quest)
            val presenter = PlayerFulfillMissionPresenter()

            playerFulfillMissionsUsecase.execute(action, presenter)

            val mission = missionRepository.findPlayerMissionByQuestId(player.id, quest.id)

            Assertions.assertThat(mission).isNotNull
            Assertions.assertThat(mission?.isCompleted()).isEqualTo(isMissionCompleted)
        }
    }

    private fun Player.acceptQuest(quest: Quest) {
        playerRepository.savePlayer(this)
        missionRepository.saveMission(Mission(this, quest))
    }
}
