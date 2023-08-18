package tw.waterballsa.utopia.utopiagamificationquest.listeners

import dev.minn.jda.ktx.interactions.components.button
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.ClaimMissionRewardService

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
    private val claimMissionRewardService: ClaimMissionRewardService,
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

            val player = user.toPlayer() ?: return

            val request = ClaimMissionRewardService.Request(player, questId)
            val presenter = object : ClaimMissionRewardService.Presenter {
                override fun presentPlayerExpNotification(mission: Mission) {
                    publishMessage(
                        """
                        ${player.name} 已獲得 ${mission.quest.reward.exp} exp！！
                        目前等級：${player.level}
                        目前經驗值：${player.exp} / ${player.currentLevelExpLimit}
                        """.trimIndent()
                    )
                }

                override fun presentNextMission(mission: Mission) {
                    mission.publishToUser(user)
                }

                override fun presentRewardsNotAllowed(mission: Mission) {
                    publishMessage("已經領取過 ${mission.quest.title} 的任務獎勵了，不能再領了")
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

    private fun ButtonInteractionEvent.publishMessage(message: String) = hook.editOriginal(message).queue()
}
