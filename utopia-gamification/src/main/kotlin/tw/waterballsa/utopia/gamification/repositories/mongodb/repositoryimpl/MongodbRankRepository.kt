package tw.waterballsa.utopia.gamification.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.repositories.RankRepository
import tw.waterballsa.utopia.mongo.gateway.MongoCollection

import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id


import tw.waterballsa.utopia.gamification.dailyrank.domain.Rank


@Component
class MongodbRankRepository(
    private val rankRepository: MongoCollection<RankDocument, String>
) : RankRepository {
    override fun save(rank: Rank): Rank {
        return rankRepository.save(rank.toDocument()).toDomain()
    }

    override fun findRank(): Rank {
        return rankRepository.findAll().first().toDomain()
    }

    private fun RankDocument.toDomain(): Rank {
//        val rankPlayers = playerIds.map { Rank.Player(it, 0,0) }
        return Rank()
    }



    private fun Rank.toDocument(): RankDocument = RankDocument(
        "id",
        emptyList()
    )
}

@Document
data class RankDocument(
    @Id val id: String,
    val playerIds: List<String>
)
