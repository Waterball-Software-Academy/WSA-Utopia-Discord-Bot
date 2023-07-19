package tw.waterballsa.utopia.utopiagamificationquest.repositories.document

import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import java.util.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Document("Mission")
class MissionDocument(
    @Id val id: String,
    // TODO 等到 @DBRef 功能上線後，將 playerId 改成 player，讓 MongoDB 協助 join
    val playerId: String,
    val questId: Int,
    val time: String,
    val state: State
)

enum class State {
    IN_PROGRESS,
    COMPLETED,
    CLAIMED
}
