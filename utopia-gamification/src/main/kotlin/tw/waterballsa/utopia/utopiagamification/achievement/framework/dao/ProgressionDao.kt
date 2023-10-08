package tw.waterballsa.utopia.utopiagamification.achievement.framework.dao

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Criteria
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.mongo.gateway.Query
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.ProgressionRepository
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.*
import tw.waterballsa.utopia.utopiagamification.achievement.framework.dao.documents.ProgressionDocument
import java.util.UUID.randomUUID

@Component
class ProgressionDao(
    private val repository: MongoCollection<ProgressionDocument, String>
) : ProgressionRepository {

    override fun findByPlayerIdAndAchievementType(
        playerId: String,
        type: Type
    ): Map<Name, Progression> {
        return repository.find(
            Query(
                Criteria("playerId").`is`(playerId)
                    .and("achievementType").`is`(type)
            )
        ).associate { it.achievementName to it.toDomain() }
    }

    override fun save(progression: Progression) {
        repository.save(progression.toDocument())
    }

    private fun Progression.toDocument(): ProgressionDocument =
        ProgressionDocument(id, playerId, type, name, count)
}
