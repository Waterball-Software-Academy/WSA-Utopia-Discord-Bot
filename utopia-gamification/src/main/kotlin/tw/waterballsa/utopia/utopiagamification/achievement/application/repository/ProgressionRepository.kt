package tw.waterballsa.utopia.utopiagamification.achievement.application.repository

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.*

interface ProgressionRepository {

    fun findByPlayerIdAndAchievementType(playerId: String, type: Type): Map<Name, Progression>

    fun save(progression: Progression)
}
