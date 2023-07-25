package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player

import java.time.OffsetDateTime

interface PlayerRepository {
    fun findPlayerById(id: String): Player?
    fun savePlayer(player: Player): Player
}

@Component
class MongoPlayerRepository(private val playerRepository: MongoCollection<PlayerDocument, String>) : PlayerRepository {

    override fun findPlayerById(id: String): Player? = playerRepository.findOne(id)?.toDomain()

    override fun savePlayer(player: Player): Player = playerRepository.save(player.toDocument()).toDomain()

    private fun Player.toDocument(): PlayerDocument = PlayerDocument(
        id,
        name,
        exp.toInt(),
        level.toInt(),
        jdaRoles,
        joinDate,
        latestActivateDate,
        levelUpgradeDate,
    )
}

@Document
data class PlayerDocument(
    @Id val id: String,
    val name: String,
    val exp: Int,
    val level: Int,
    //TODO 因為 jda 可以撈到 roles，所以 document 不用多存 jda roles
    val roles: List<String>,
    val joinDate: OffsetDateTime,
    val latestActivateDate: OffsetDateTime,
    val levelUpgradeDate: OffsetDateTime,
) {
    fun toDomain(): Player = Player(
        id,
        name,
        exp.toULong(),
        level.toUInt(),
        roles.toMutableList(),
        joinDate,
        latestActivateDate,
        levelUpgradeDate
    )
}
