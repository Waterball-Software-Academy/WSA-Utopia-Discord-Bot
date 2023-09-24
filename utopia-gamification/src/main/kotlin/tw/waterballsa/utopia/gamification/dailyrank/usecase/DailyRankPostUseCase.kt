package tw.waterballsa.utopia.gamification.dailyrank.usecase

import tw.waterballsa.utopia.gamification.dailyrank.domain.Settlement
import tw.waterballsa.utopia.gamification.repositories.DailyEventRepository
import tw.waterballsa.utopia.gamification.repositories.RankRepository

class DailyRankPostUseCase(
    val dailyEventRepository: DailyEventRepository,
    val rankRepository: RankRepository
) {
    fun execute(presenter: Presenter) {

    }

    interface Presenter {
        fun present(settlements: List<Settlement>)
    }
}
