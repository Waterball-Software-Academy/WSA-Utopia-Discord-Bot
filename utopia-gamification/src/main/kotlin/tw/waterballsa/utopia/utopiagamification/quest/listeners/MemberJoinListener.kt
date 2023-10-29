package tw.waterballsa.utopia.utopiagamification.quest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.QuestIds.Companion.unlockAcademyQuestId
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.AssignPlayerQuestPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.AssignPlayerQuestUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

@Component
class MemberJoinListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val assignPlayerQuestUsecase: AssignPlayerQuestUsecase,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        with(event) {
            //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
            val player = user.toPlayer() ?: return

            val request = AssignPlayerQuestUsecase.Request(user.id, unlockAcademyQuestId)
            val presenter = AssignPlayerQuestPresenter()

            assignPlayerQuestUsecase.execute(request, presenter)

            val viewModel = presenter.viewModel ?: return

            viewModel.publishToUser(user)
        }
    }
}
