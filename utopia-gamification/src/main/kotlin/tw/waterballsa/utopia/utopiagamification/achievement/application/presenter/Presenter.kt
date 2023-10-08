package tw.waterballsa.utopia.utopiagamification.achievement.application.presenter

import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent

interface Presenter {
    fun present(event: List<AchievementAchievedEvent>)
}
