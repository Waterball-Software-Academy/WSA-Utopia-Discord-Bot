package tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters

import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase


class PlayerFulfillMissionPresenter : PlayerFulfillMissionsUsecase.Presenter {

    lateinit var viewModel: ViewModel
        private set

    override fun present(mission: Mission) {
        viewModel = ViewModel(mission.quest.postMessage, mission.quest.id)
    }

    class ViewModel(
        val postMessage: String,
        val questId: Int
    )
}
