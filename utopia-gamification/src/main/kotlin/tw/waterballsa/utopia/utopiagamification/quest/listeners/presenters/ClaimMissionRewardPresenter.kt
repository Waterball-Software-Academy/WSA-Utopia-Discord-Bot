package tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters

import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet
import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.Companion.toLevelRange
import tw.waterballsa.utopia.utopiagamification.quest.usecase.ClaimMissionRewardUsecase

class ClaimMissionRewardPresenter : ClaimMissionRewardUsecase.Presenter {

    var viewModel: ViewModel? = null
        private set

    override fun presentPlayerExpNotification(mission: Mission) {
        viewModel = ViewModel(
            """
            ${mission.player.name} 已獲得 ${mission.quest.reward.exp} exp！！
            目前等級：${mission.player.level}
            目前經驗值：${mission.player.currentExp()}/${mission.player.level.toLevelRange().expLimit}
            """.trimIndent(),
            mission.nextQuestId()
        )
    }

    data class ViewModel(
        val message: String,
        val nextQuestId: Int?
    )

    private fun Player.currentExp(): ULong =
        if (level == LevelSheet.LevelRange.LEVEL_ONE.level.toUInt()) exp else exp - level.toLevelRange().previous!!.accExp
}
