package tw.waterballsa.utopia.utopiagmification.leaderboard.it

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import tw.waterballsa.utopia.gamification.leaderboard.domain.LeaderBoardItem
import tw.waterballsa.utopia.utopiagamification.leaderboard.repository.LeaderBoardRepository
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamification.repositories.query.PageRequest
import tw.waterballsa.utopia.utopiagamification.repositories.query.Pageable
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest


@UtopiaTest
class LeaderBoardIntegrationTest @Autowired constructor(
    private val playerRepository: PlayerRepository,
    private val leaderBoardRepository: LeaderBoardRepository,
) {

    @Test
    @DisplayName(
        """
        test player rank query
            Given：
                - setup 3 Players data, Jack is test target
                | Name  | Lv | Exp  | Bounty |
                | ----- | -- | ---- | ------ |
                | Jack  |  6 | 1870 |      0 |
                | Wally |  4 | 1100 |      0 |
                | Mily  | 11 | 2912 |      0 |
            When：
                - query Jack's rank
            Then：
                - get Jack leaderboard item
    """
    )
    fun `test player rank query`() {
        // Given
        val jack = playerRepository.savePlayer(Player(id = "1", name = "Jack", level = 6u, exp = 1870u))
        playerRepository.savePlayer(Player(id = "2", name = "Wally", level = 4u, exp = 1100u))
        playerRepository.savePlayer(Player(id = "3", name = "Mily", level = 11u, exp = 2912u))

        // When
        val jackLeaderBoardItem = leaderBoardRepository.queryPlayerRank(jack.id)

        // Then
        assertThat(jackLeaderBoardItem).isNotNull
        assertThat(jackLeaderBoardItem!!.name).isEqualTo(jack.name)
        assertThat(jackLeaderBoardItem.exp).isEqualTo(jack.exp)
        assertThat(jackLeaderBoardItem.level).isEqualTo(jack.level)
        assertThat(jackLeaderBoardItem.rank).isEqualTo(2)
    }

    @Test
    @DisplayName(
        """
            test leaderboard query
                Given：
                    - setup 12 players data
                    | Name  | Lv | Exp  | Bounty |
                    | ----- | -- | ---- | ------ |
                    | Jack  | 11 | 5600 |      0 |
                    | 張無忌 | 10 | 4500 |      0 |
                    | Mily  |  8 | 2912 |      0 |
                    | 咪五 　|  7 | 2200 |      0 |
                    | M1    |  6 | 2000 |      0 |
                    | M2    |  6 | 1888 |      0 |
                    | self  |  6 | 1870 |      0 |
                    | 咪四 　|  5 | 1200 |      0 |
                    | Wally |  5 | 1100 |      0 |
                    | fin   |  5 | 1000 |      0 |
                    | 阿瓜 　|  4 |  675 |      0 |
                    | 咪六 　|  4 |  675 |      0 |
                When：
                    - query the page of the leaderboard with 10 items
                Then：
                    - get the 10 leaderboard items of the page
        """
    )
    fun `test leaderboard query`() {
        // Given
        prepareLeaderBoard()
        val firstPage = PageRequest.of(0, 10)

        // When & Then
        assertLeaderBoardItems(firstPage,
            LeaderBoardItem("1", "Jack", 5600u, 11u, 0u, 1),
            LeaderBoardItem("2", "張無忌", 4500u, 10u, 0u, 2),
            LeaderBoardItem("3", "Mily", 2912u, 8u, 0u, 3),
            LeaderBoardItem("4", "咪五", 2200u, 7u, 0u, 4),
            LeaderBoardItem("5", "M1", 2000u, 6u, 0u, 5),
            LeaderBoardItem("6", "M2", 1888u, 6u, 0u, 6),
            LeaderBoardItem("7", "self", 1870u, 6u, 0u, 7),
            LeaderBoardItem("8", "咪四", 1200u, 5u, 0u, 8),
            LeaderBoardItem("9", "Wally", 1100u, 5u, 0u, 9),
            LeaderBoardItem("10", "fin", 1000u, 5u, 0u, 10)
        )

        // When & Then
        val secondPage = firstPage.next()
        assertLeaderBoardItems(secondPage,
            LeaderBoardItem("11", "阿瓜", 675u, 4u, 0u, 11),
            LeaderBoardItem("12", "咪六", 675u, 4u, 0u, 12)
        )

        // When & Then
        val thirdPage = secondPage.next()
        assertLeaderBoardItems(thirdPage)
    }

    private fun prepareLeaderBoard() {
        listOf(
            Player("1", "Jack", 5600u),
            Player("2", "張無忌", 4500u),
            Player("3", "Mily", 2912u),
            Player("4", "咪五", 2200u),
            Player("5", "M1", 2000u),
            Player("6", "M2", 1888u),
            Player("7", "self", 1870u),
            Player("8", "咪四", 1200u),
            Player("9", "Wally", 1100u),
            Player("10", "fin", 1000u),
            Player("11", "阿瓜", 675u),
            Player("12", "咪六", 675u),
        ).shuffled()
            .forEach { playerRepository.savePlayer(it) }
    }

    private fun assertLeaderBoardItems(pageable: Pageable, vararg expectedLeaderBoardItems: LeaderBoardItem) {
        val page = leaderBoardRepository.findAll(pageable)
        val actualLeaderBoardItems = page.getContent()
        assertThat(actualLeaderBoardItems).isEqualTo(expectedLeaderBoardItems.toList())
    }
}
