package tw.waterballsa.utopia.gamification.repositories

import tw.waterballsa.utopia.gamification.dailyrank.DailyEventHandler
import tw.waterballsa.utopia.gamification.dailyrank.domain.DailyEvent

interface DailyEventRepository : DailyEventHandler{
    fun findNotPostedEvent() : List<DailyEvent>
}
