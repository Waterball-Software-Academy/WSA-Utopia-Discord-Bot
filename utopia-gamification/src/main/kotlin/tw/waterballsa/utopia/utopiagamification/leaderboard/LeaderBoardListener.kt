package tw.waterballsa.utopia.utopiagamification.leaderboard

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.leaderboard.domain.LeaderBoardItem
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamification.leaderboard.repository.LeaderBoardRepository
import tw.waterballsa.utopia.utopiagamification.repositories.query.Page
import tw.waterballsa.utopia.utopiagamification.repositories.query.PageRequest
import tw.waterballsa.utopia.utopiagamification.repositories.query.Pageable

private const val UTOPIA_COMMAND_NAME = "utopia"
private const val LEADERBOARD_PREVIOUS_BUTTON = "utopia-leaderboard-previous"
private const val LEADERBOARD_NEXT_BUTTON = "utopia-leaderboard-next"
private const val LEADERBOARD_OPTION = "options"
private const val LEADERBOARD_SUBCOMMAND_NAME = "leaderboard"
private const val LEADERBOARD_MY_RANK = "my-rank"
private const val PREVIOUS_PAGE = "上一頁"
private const val NEXT_PAGE = "下一頁"

@Component
class LeaderBoardListener(
    private val leaderBoardRepository: LeaderBoardRepository,
) : UtopiaListener() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        with(event) {
            if (name != UTOPIA_COMMAND_NAME || subcommandName != LEADERBOARD_SUBCOMMAND_NAME) {
                return
            }

            val isLeaderboardQuery = options.isEmpty()
            if (isLeaderboardQuery) {
                queryLeaderboard()
            }

            val leaderboardOption = getOption(LEADERBOARD_OPTION)?.asString ?: return

            val isSelfRankQuery = leaderboardOption == LEADERBOARD_MY_RANK
            if (isSelfRankQuery) {
                querySelfRank()
            }
        }
    }

    /**
     * 使用 Discord Embedded Message：
     * - 印出多列：`<rank> <@userId> Lv.<等級> Exp: <經驗值> $<賞金>` ，每一列代表一個排名。
     * Discord Embedded Message 下方有兩個按鈕，”Previous Page” 和 “Next Page”。
     */
    private fun SlashCommandInteractionEvent.queryLeaderboard() {
        val pageable = PageRequest.of(0, 10)
        val page = leaderBoardRepository.findAll(pageable)

        reply("").addEmbeds(
            Embed { description = page.createLeaderBoardRankDescription() }
        ).addActionRow(
            createLeaderBoardButtons(page)
        ).queue()
    }

    /**
     * 假設是 leaderboard my-rank 指令的話，會去 query 指定的 player
     * 然後 output「妳的排名為第 N 名」到 discord channel
     */
    private fun SlashCommandInteractionEvent.querySelfRank() {
        val rank = leaderBoardRepository.queryPlayerRank(user.id)?.let {
            "你的排名為第 ${it.rank} 名"
        } ?: "找不到你的排名"
        reply("").addEmbeds(
            Embed { description = rank }
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            if (!button.isLeaderBoardButton()) {
                return
            }

            deferEdit().queue()

            val pageable: Pageable = button.toPageable()
            val page = leaderBoardRepository.findAll(pageable)

            hook.editMessageEmbedsById(
                messageId,
                Embed {
                    description = page.createLeaderBoardRankDescription()
                }
            ).setActionRow(
                createLeaderBoardButtons(page)
            ).queue()
        }
    }

    private fun Button.isLeaderBoardButton(): Boolean {
        return listOf(
            LEADERBOARD_PREVIOUS_BUTTON,
            LEADERBOARD_NEXT_BUTTON
        ).any { id.toString().contains(it) }
    }

    private fun Button.toPageable(): Pageable {
        val (_, page, size) = id.toString().split("_")
        return PageRequest.of(page.toInt(), size.toInt())
    }

    private fun Page<LeaderBoardItem>.createLeaderBoardRankDescription(): String {
        return getContent().joinToString(separator = "\n") {
            "[${it.rank}] ${it.name}, ${it.level}, EXP=${it.exp}, Bounty=${it.bounty}"
        }
    }

    private fun createLeaderBoardButtons(page: Page<LeaderBoardItem>): List<Button> =
        mutableListOf(createPreviousPageButton(page), createNextPageButton(page))

    private fun createPreviousPageButton(page: Page<LeaderBoardItem>): Button {
        return if (page.hasPrevious()) {
            Button.primary(
                createPageButtonId(LEADERBOARD_PREVIOUS_BUTTON, page.previousPageable()),
                PREVIOUS_PAGE
            ).asEnabled()
        } else {
            Button.primary(
                createPageButtonId(LEADERBOARD_PREVIOUS_BUTTON, page.getPageable()),
                PREVIOUS_PAGE
            ).asDisabled()
        }
    }

    private fun createNextPageButton(page: Page<LeaderBoardItem>): Button {
        return if (page.hasNext()) {
            Button.primary(
                createPageButtonId(LEADERBOARD_NEXT_BUTTON, page.nextPageable()),
                NEXT_PAGE
            ).asEnabled()
        } else {
            Button.primary(
                createPageButtonId(LEADERBOARD_NEXT_BUTTON, page.getPageable()),
                NEXT_PAGE
            ).asDisabled()
        }
    }

    private fun createPageButtonId(prefix: String, pageable: Pageable): String {
        return "${prefix}_${pageable.getPageNumber()}_${pageable.getPageSize()}"
    }
}
