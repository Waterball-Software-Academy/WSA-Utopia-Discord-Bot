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
