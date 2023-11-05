package tw.waterballsa.utopia.utopiagamification.quest.listeners

import dev.minn.jda.ktx.interactions.components.button
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.AssignPlayerQuestPresenter
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.ClaimMissionRewardPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.AssignPlayerQuestUsecase
import tw.waterballsa.utopia.utopiagamification.quest.usecase.ClaimMissionRewardUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository

const val BUTTON_QUEST_TAG = "quest"

class RewardButton {
    companion object {
        const val NAME = "rewardButton"
        const val LABEL: String = "領取獎勵"

        fun id(questId: Int): String = "$BUTTON_QUEST_TAG-$NAME-$questId"
    }
}

@Component
class UtopiaGamificationQuestListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val claimMissionRewardUsecase: ClaimMissionRewardUsecase,
    private val assignPlayerQuestUsecase: AssignPlayerQuestUsecase
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, buttonName, questId) = splitButtonId("-").ifEmpty { return }

            if (buttonTag != BUTTON_QUEST_TAG && buttonName != RewardButton.NAME) {
                return
            }

            deferReply().queue {
                editButton(button("null", "已領取", style = ButtonStyle.SUCCESS).asDisabled()).queue()
            }

            //TODO 這個 toPlayer 會有副作用，會註冊玩家，之後會發 pr 解決這個問題
            val player = user.toPlayer() ?: return

            val request = ClaimMissionRewardUsecase.Request(user.id, questId.toInt())
            val presenter = ClaimMissionRewardPresenter()

            claimMissionRewardUsecase.execute(request, presenter)

            val viewModel = presenter.viewModel ?: return
            hook.editOriginal(viewModel.message).queue()

            if (viewModel.nextQuestId != null) {
                val assignNextQuestRequest = AssignPlayerQuestUsecase.Request(user.id, viewModel.nextQuestId)
                val assignNextQuestPresenter = AssignPlayerQuestPresenter()

                assignPlayerQuestUsecase.execute(assignNextQuestRequest, assignNextQuestPresenter)

                assignNextQuestPresenter.viewModel?.publishToUser(user)
            }
        }
    }

    private fun ButtonInteractionEvent.splitButtonId(delimiters: String): List<String> {
        val result = button.id?.split(delimiters) ?: return emptyList()
        if (result.size != 3) {
            return emptyList()
        }
        return result
    }
}
