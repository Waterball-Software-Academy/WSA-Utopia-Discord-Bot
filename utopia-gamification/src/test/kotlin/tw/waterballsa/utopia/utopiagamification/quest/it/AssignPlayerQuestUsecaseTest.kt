package tw.waterballsa.utopia.utopiagamification.quest.it

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.exception.AssignedQuestException
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.QuestIds.Companion.unlockAcademyQuestId
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.AssignPlayerQuestPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.AssignPlayerQuestUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest

@UtopiaTest
class AssignPlayerQuestUsecaseTest @Autowired constructor(
    private val assignPlayerQuestUsecase: AssignPlayerQuestUsecase,
    private val playerRepository: PlayerRepository,
    private val missionRepository: MissionRepository,
    private val questRepository: QuestRepository
) {

    private val playerA = Player("A", "A")
    private val request = AssignPlayerQuestUsecase.Request(playerId = playerA.id, questId = unlockAcademyQuestId)
    private val presenter = AssignPlayerQuestPresenter()

    @BeforeEach
    fun setup() {
        playerRepository.savePlayer(playerA)
    }

    @Test
    fun `given playerA do not have first quest when playerA assign first quest then playerA get first quest`() {
        //when
        assertThatNoException().isThrownBy {
            assignPlayerQuestUsecase.execute(request, presenter)
        }

        //then
        val missions = missionRepository.findAllByPlayerId(playerA.id)
        assertThat(missions).hasSize(1)
        val mission = missions.first()
        assertThat(mission.player.id).isEqualTo(playerA.id)
        assertThat(mission.quest.id).isEqualTo(unlockAcademyQuestId)

        val quest = questRepository.findById(request.questId)
        val viewModel = presenter.viewModel
        assertThat(viewModel).isNotNull
        assertThat(viewModel?.questTitle).isEqualTo(quest?.title)
        assertThat(viewModel?.questDescription).isEqualTo(quest?.description)
    }

    @Test
    fun `given playerA has first quest when playerA assign first quest then playerA can't get same quest repeatedly`() {
        //when
        assertThatNoException().isThrownBy {
            assignPlayerQuestUsecase.execute(request, presenter)
        }

        //then
        assertThatExceptionOfType(AssignedQuestException::class.java).isThrownBy {
            assignPlayerQuestUsecase.execute(request, presenter)
        }
    }

    @Test
    fun `when assign a quest to the non-exist player, then should be fail`() {
        //given
        val nonExistPlayerId = "B"
        val failRequest = AssignPlayerQuestUsecase.Request(playerId = nonExistPlayerId, questId = unlockAcademyQuestId)

        //then
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
            assignPlayerQuestUsecase.execute(failRequest, presenter)
        }
    }

    @Test
    fun `when assign a non-exist quest to the player A, then should be fail`() {
        //given
        val notExistQuestId = -1
        val failRequest = AssignPlayerQuestUsecase.Request(playerId = playerA.id, questId = notExistQuestId)

        //then
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
            assignPlayerQuestUsecase.execute(failRequest, presenter)
        }
    }
}
