package tw.waterballsa.utopia.utopiagamificationquest.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.State
import tw.waterballsa.utopia.utopiagamificationquest.domain.State.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import java.time.LocalDateTime
import java.util.UUID.fromString

@Component
class MongodbMissionRepository(
    private val repository: MongoCollection<MissionDocument, String>,
    private val playerRepository: PlayerRepository,
    private val quests: Quests
) : MissionRepository {

    override fun findPlayerMissionByQuestId(playerId: String, questId: Int): Mission? = repository.find(
        Query(
            Criteria("playerId").`is`(playerId).and("questId").`is`(questId)
        )
    ).firstOrNull()?.toDomain()

    override fun findInProgressMissionsByPlayerId(playerId: String): List<Mission> = repository.find(
        Query(
            Criteria("playerId").`is`(playerId).and("state").`is`(IN_PROGRESS)
        )
    ).map { it.toDomain() }

    override fun findAllByPlayerId(playerId: String): List<Mission> = repository.find(
        Query(
            Criteria("playerId").`is`(playerId)
        )
    ).map { it.toDomain() }

    override fun findAllByQuestId(questId: Int): List<Mission> = repository.find(
        Query(
            Criteria("questId").`is`(questId)
        )
    ).map { it.toDomain() }

    override fun saveMission(mission: Mission): Mission {
        playerRepository.savePlayer(mission.player)
        repository.save(mission.toDocument())
        return mission
    }

    // TODO 等到 @DBRef 功能上線後，將 playerId 改成 player，讓 MongoDB 協助 join
    private fun MissionDocument.toDomain(): Mission {
        val player = playerRepository.findPlayerById(playerId) ?: throw RuntimeException("not find player")
        val quest = quests.findById(questId)
        return Mission(fromString(id), player, quest, state, completedTime)
    }

    private fun Mission.toDocument(): MissionDocument =
        MissionDocument(id.toString(), player.id, quest.id, completedTime, state)
}

@Document("Mission")
class MissionDocument(
    @Id val id: String,
    // TODO 等到 @DBRef 功能上線後，將 playerId 改成 player，讓 MongoDB 協助 join
    val playerId: String,
    val questId: Int,
    val completedTime: LocalDateTime?,
    val state: State
)
