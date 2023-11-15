package tw.waterballsa.utopia.utopiagamification.leaderboard.repository

import tw.waterballsa.utopia.gamification.leaderboard.domain.LeaderBoardItem
import tw.waterballsa.utopia.utopiagamification.repositories.PageableRepository

interface LeaderBoardRepository: PageableRepository<LeaderBoardItem> {

    fun queryPlayerRank(playerId: String): LeaderBoardItem?

}
