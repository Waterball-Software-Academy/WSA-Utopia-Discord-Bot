package tw.waterballsa.utopia.utopiagamification.achievement.application.repository

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement

interface AchievementRepository {

    fun findByType(type: Achievement.Type): List<Achievement>
}
