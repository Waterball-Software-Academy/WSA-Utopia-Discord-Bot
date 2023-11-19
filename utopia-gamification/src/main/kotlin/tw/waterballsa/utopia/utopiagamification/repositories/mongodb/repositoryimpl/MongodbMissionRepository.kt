package tw.waterballsa.utopia.utopiagamification.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.*
import tw.waterballsa.utopia.utopiagamification.quest.domain.Mission
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.domain.State
import tw.waterballsa.utopia.utopiagamification.quest.domain.State.*
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository
import tw.waterballsa.utopia.utopiagamification.repositories.exceptions.NotFoundException.Companion.notFound
import java.time.LocalDateTime
import java.util.UUID.fromString

@Component
class MongodbMissionRepository(
    private val repository: MongoCollection<MissionDocument, String>,
    private val playerRepository: PlayerRepository,
    private val questRepository: QuestRepository
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
        repository.save(mission.toDocument())
        return mission
    }

    override fun existMission(playerId: String, questId: Int): Boolean =
        findPlayerMissionByQuestId(playerId, questId) != null

    // TODO 等到 @DBRef 功能上線後，將 playerId 改成 player，讓 MongoDB 協助 join
    private fun MissionDocument.toDomain(): Mission {
        val player = playerRepository.findPlayerById(playerId)
            ?: throw notFound(Player::class).id(playerId).message("mission document to domain").build()
        val quest = questRepository.findById(questId)
            ?: throw notFound(Quest::class).id(questId).message("mission document to domain").build()
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
