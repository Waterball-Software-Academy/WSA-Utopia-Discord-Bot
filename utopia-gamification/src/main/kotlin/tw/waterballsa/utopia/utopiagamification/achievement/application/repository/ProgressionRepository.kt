package tw.waterballsa.utopia.utopiagamification.achievement.application.repository

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Progression

interface ProgressionRepository {

    fun findByPlayerIdAndAchievementType(playerId: String, type: Achievement.Type): List<Progression>

    fun save(progression: Progression): Progression
}
