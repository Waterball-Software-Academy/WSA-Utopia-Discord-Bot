package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.domain.quests.Quests
import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.MissionDocument
import tw.waterballsa.utopia.utopiagamificationquest.repositories.document.State.COMPLETED
import java.util.UUID.fromString

@Component
class MongodbMissionRepository(
    private val repository: MongoCollection<MissionDocument, String>,
    private val playerRepository: PlayerRepository,
    private val quests: Quests
) : MissionRepository {

    override fun findMission(query: MissionRepository.Query): Mission? =
        repository.findAll()
            .map { it.toDomain() }
            .find { query.match(it) }

    //TODO 等 mongodb gateway 的 query 上線之後，把 findAll() 改成 query
    override fun findIncompleteMissionsByPlayerId(playerId: String): List<Mission> =
        repository.findAll()
            .filter { it.playerId == playerId && it.state != COMPLETED }
            .map { it.toDomain() }

    override fun findAllByPlayerId(playerId: String): List<Mission> =
        repository.findAll()
            .filter { it.playerId == playerId }
            .map { it.toDomain() }


    override fun saveMission(mission: Mission): Mission {
        repository.save(mission.toDocument())
        return mission
    }

    // TODO 等到 @DBRef 功能上線後，將 playerId 改成 player，讓 MongoDB 協助 join
    private fun MissionDocument.toDomain(): Mission {
        val player = playerRepository.findPlayerById(playerId) ?: playerRepository.savePlayer(Player(playerId, "123"))
        val quest = quests.findById(questId)
        return Mission(fromString(id), player, quest, state, completedTime)
    }
}

fun Mission.toDocument(): MissionDocument =
    MissionDocument(id.toString(), player.id, quest.id, completedTime, state)
