package tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.presenter

import tw.waterballsa.utopia.utopiagamification.achievement.application.presenter.Presenter
import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType

class ProgressAchievementPresenter : Presenter {
    val progressAchievementViewModels = mutableListOf<ProgressAchievementViewModel>()

    override fun present(event: List<AchievementAchievedEvent>) {
        for (achievedEvent in event) {
            progressAchievementViewModels.add(achievedEvent.toViewModel())
        }
    }

    fun toMessage(): String = progressAchievementViewModels.joinToString("\n") {
        "恭喜達成 **_${it.roleType.description}_** 成就，取得 **_${it.exp}_** 點經驗值！"
    }

    fun isViewModelsNotEmpty(): Boolean = progressAchievementViewModels.isNotEmpty()

    private fun AchievementAchievedEvent.toViewModel(): ProgressAchievementViewModel =
        ProgressAchievementViewModel(reward.exp.toLong(), reward.role!!)

    data class ProgressAchievementViewModel(
        val exp: Long,
        val roleType: RoleType,
    )
}
