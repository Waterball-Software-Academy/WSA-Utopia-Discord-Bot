package tw.waterballsa.utopia.utopiagamificationquest.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import java.time.OffsetDateTime

@Component
class MongodbPlayerRepository(
    private val playerRepository: MongoCollection<PlayerDocument, String>
) : PlayerRepository {

    override fun findPlayerById(id: String): Player? = playerRepository.findOne(id)?.toDomain()

    override fun savePlayer(player: Player): Player = playerRepository.save(player.toDocument()).toDomain()

    private fun PlayerDocument.toDomain(): Player = Player(
        id,
        name,
        exp.toULong(),
        level.toUInt(),
        joinDate,
        latestActivateDate,
        levelUpgradeDate
    )

    private fun Player.toDocument(): PlayerDocument = PlayerDocument(
        id,
        name,
        exp.toInt(),
        level.toInt(),
        joinDate,
        latestActivateDate,
        levelUpgradeDate
    )
}

@Document
data class PlayerDocument(
    @Id val id: String,
    val name: String,
    val exp: Int,
    val level: Int,
    val joinDate: OffsetDateTime,
    val latestActivateDate: OffsetDateTime,
    val levelUpgradeDate: OffsetDateTime,
)
