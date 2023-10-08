package tw.waterballsa.utopia.utopiagamification.achievement.framework.dao.documents

import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.*

@Document
class ProgressionDocument(
    @Id val id: String,
    val playerId: String,
    val achievementType: Type,
    val achievementName: Name,
    val count: Int
) {
    fun toDomain(): Progression = Progression(id, playerId, achievementName, achievementType, count)
}
