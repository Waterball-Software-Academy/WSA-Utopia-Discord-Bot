package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.BUTTON_QUEST_TAG
import tw.waterballsa.utopia.utopiagamificationquest.domain.buttons.RewardButton
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.ClaimMissionRewardService

@Component
class UtopiaGamificationQuestListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val claimMissionRewardService: ClaimMissionRewardService,
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (buttonTag, buttonName, questTitle) = splitButtonId("-").ifEmpty { return }

            if (buttonTag != BUTTON_QUEST_TAG && buttonName != RewardButton.NAME) {
                return
            }

            deferReply().queue()

            val player = user.toPlayer() ?: return

            val request = ClaimMissionRewardService.Request(player, questTitle)
            val presenter = object : ClaimMissionRewardService.Presenter {
                override fun presentMission(mission: Mission) {
                    publishPlayerExpNotification(mission)
                }

                override fun presentNextMission(mission: Mission) {
                    mission.publishToUser(user).queue()
                }
            }

            claimMissionRewardService.execute(request, presenter)
        }
    }

    private fun ButtonInteractionEvent.splitButtonId(delimiters: String): List<String> {
        val result = button.id?.split(delimiters) ?: return emptyList()
        if (result.size != 3) {
            return emptyList()
        }
        return result
    }

    private fun ButtonInteractionEvent.publishPlayerExpNotification(mission: Mission) =
        with(mission) {
            hook.editOriginal("""
                    ${player.name} 已獲得 ${quest.reward.exp} exp！！
                    目前等級：${player.level}
                    目前經驗值：${player.exp} / ${player.currentLevelExpLimit}
                    """.trimIndent()
            ).queue()

            editButton(mission.rewardButton.asDisabled()).queue()
        }
}