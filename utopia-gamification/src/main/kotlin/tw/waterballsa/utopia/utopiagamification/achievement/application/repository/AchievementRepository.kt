package tw.waterballsa.utopia.utopiagamification.achievement.application.repository

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type

interface AchievementRepository {

    fun findByType(type: Type): List<Achievement>
}
