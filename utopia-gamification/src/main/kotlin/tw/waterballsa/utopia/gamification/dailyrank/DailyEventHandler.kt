package tw.waterballsa.utopia.gamification.dailyrank

import tw.waterballsa.utopia.gamification.dailyrank.domain.DailyEvent

interface DailyEventHandler {

    fun save(event:DailyEvent) : DailyEvent
}
