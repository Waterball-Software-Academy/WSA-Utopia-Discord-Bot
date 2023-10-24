package tw.waterballsa.utopia.utopiagamification.achievement.framework.dao

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Criteria
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.mongo.gateway.Query
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.ProgressionRepository
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Progression
import tw.waterballsa.utopia.utopiagamification.achievement.framework.dao.documents.ProgressionDocument

@Component
class ProgressionDao(
    private val repository: MongoCollection<ProgressionDocument, String>
) : ProgressionRepository {

    override fun findByPlayerIdAndAchievementType(
            playerId: String,
            type: Achievement.Type
    ): List<Progression> {
        val query = Query(Criteria("playerId").`is`(playerId).and("achievementType").`is`(type))
        return repository.find(query)
                .map { it.toDomain() }
    }

    override fun save(progression: Progression): Progression = repository.save(progression.toDocument()).toDomain()

    private fun Progression.toDocument(): ProgressionDocument =
        ProgressionDocument(id, playerId, type, name, count)
}
