package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.actions.ButtonInteractionAction
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.BUTTON_QUEST_TAG
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.ClaimMissionRewardService
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

@Component
class UtopiaGamificationQuestListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val claimMissionRewardService: ClaimMissionRewardService,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, buttonName, args) = splitButtonId("-")

            if (buttonTag != BUTTON_QUEST_TAG) {
                return
            }

            val player = user.toPlayer() ?: return

            val action = ButtonInteractionAction(player, buttonName)

            when (action.buttonName) {
                RewardButton.NAME -> handleRewardButtonInteraction(action, args[0])
                else -> {
                    playerFulfillMissionsService.execute(action, user.presenter)
                }
            }
        }
    }

    private fun ButtonInteractionEvent.splitButtonId(delimiters: String): Triple<String, String, List<String>> {
        val result = button.id?.split(delimiters) ?: return Triple("", "", listOf())

        val tag = result.getOrElse(0) { "" }
        val buttonName = result.getOrElse(1) { "" }
        val args = if (result.size > 2) result.drop(2) else listOf()

        return Triple(tag, buttonName, args)
    }

    private fun ButtonInteractionEvent.handleRewardButtonInteraction(
        action: ButtonInteractionAction,
        questTitle: String
    ) {

        val request = ClaimMissionRewardService.Request(action.player, questTitle)
        val presenter = object : ClaimMissionRewardService.Presenter {
            override fun presentMission(mission: Mission) {
                publishPlayerExpNotification(mission)
            }

            override fun presentNextMission(mission: Mission) {
                mission.publishToUser(user).complete()
            }
        }
        claimMissionRewardService.execute(request, presenter)
    }

    private fun ButtonInteractionEvent.publishPlayerExpNotification(mission: Mission) =
        with(mission) {
            reply(
                """
                    ${player.name} 已獲得 ${quest.reward.exp} exp!!
                    目前等級：${player.level}
                    目前經驗值：${player.exp}
                    """.trimIndent()
            ).complete()
        }
}


