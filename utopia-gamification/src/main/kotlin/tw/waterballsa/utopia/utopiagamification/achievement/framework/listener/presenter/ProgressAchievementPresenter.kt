package tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.presenter

import tw.waterballsa.utopia.utopiagamification.achievement.application.presenter.Presenter
import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType

class ProgressAchievementPresenter : Presenter {

    lateinit var viewModels: List<ProgressAchievementViewModel>
        private set

    override fun present(events: List<AchievementAchievedEvent>) {
        viewModels = events.map { it.toViewModel() }
    }

    fun toAchievementAchievedNotification(): String = viewModels.joinToString("\n") {
        "恭喜達成 **_${it.roleType.description}_** 成就，取得 **_${it.exp}_** 點經驗值！"
    }

    fun isAchievementAchieved(): Boolean = viewModels.isNotEmpty()

    private fun AchievementAchievedEvent.toViewModel(): ProgressAchievementViewModel =
        ProgressAchievementViewModel(reward.exp.toLong(), reward.role!!)

    data class ProgressAchievementViewModel(
        val exp: Long,
        val roleType: RoleType,
    )
}
