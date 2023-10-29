package tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters

import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.usecase.AssignPlayerQuestUsecase

class AssignPlayerQuestPresenter : AssignPlayerQuestUsecase.Presenter {

    var viewModel: ViewModel? = null
        private set

    override fun presentMission(mission: Mission) {
        viewModel = mission.toViewModel()
    }

    private fun Mission.toViewModel(): ViewModel = ViewModel(
        quest.title,
        quest.description,
        "${quest.criteria}".replace(Regex("\\n{2,}"), "\n"),
        quest.link
    )

    data class ViewModel(
        val questTitle: String,
        val questDescription: String,
        val criteria: String,
        val link: String,
        val assignQuestMessage: String = "你已接取任務，到私訊查看內容吧！"
    )
}
