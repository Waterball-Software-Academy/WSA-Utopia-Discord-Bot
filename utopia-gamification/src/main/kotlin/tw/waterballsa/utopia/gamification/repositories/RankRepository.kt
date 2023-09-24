package tw.waterballsa.utopia.gamification.repositories

import tw.waterballsa.utopia.gamification.dailyrank.domain.Rank

interface RankRepository {
    fun save(rank: Rank): Rank
    fun findRank(): Rank
}
