package tw.waterballsa.utopia.gamification.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.leaderboard.domain.LeaderBoardItem
import tw.waterballsa.utopia.utopiagamification.leaderboard.repository.LeaderBoardRepository
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.page
import tw.waterballsa.utopia.utopiagamification.repositories.query.Page
import tw.waterballsa.utopia.utopiagamification.repositories.query.Pageable

@Component
class MongodbLeaderBoardRepository(
    private val playerRepository: PlayerRepository
) : LeaderBoardRepository {

    override fun findAll(pageable: Pageable): Page<LeaderBoardItem> = playerRepository.findAll()
        .rank()
        .page(pageable)

    override fun queryPlayerRank(playerId: String): LeaderBoardItem? = playerRepository.findAll()
        .rank()
        .find { it.playerId == playerId }

}


private fun Collection<Player>.rank(): List<LeaderBoardItem> =
    sortedWith(rankOrder)
        .mapIndexed { index, it -> LeaderBoardItem(it.id, it.name, it.exp, it.level, it.bounty.toUInt(), index + 1) }

private val rankOrder: Comparator<Player> =
    compareByDescending<Player> { it.level }
        .thenByDescending { it.exp }
        .thenByDescending { it.bounty }
        .thenBy { it.levelUpgradeDate }
        .thenBy { it.joinDate }
        .thenBy { it.id }
