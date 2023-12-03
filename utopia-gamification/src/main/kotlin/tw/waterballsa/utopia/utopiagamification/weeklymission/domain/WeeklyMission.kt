package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

open class WeeklyMission (
        var status :Status = Status.PROGRESS,
){
    lateinit var reward: Reward

    enum class Status{
        COMPLETE,
        PROGRESS
    }
}
