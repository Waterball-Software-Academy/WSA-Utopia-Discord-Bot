package tw.waterballsa.utopia.gamification.quest.listeners

import dev.minn.jda.ktx.interactions.components.button
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.Button
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.gamification.quest.domain.Mission
import tw.waterballsa.utopia.gamification.quest.domain.Player
import tw.waterballsa.utopia.gamification.repositories.PlayerRepository
import tw.waterballsa.utopia.gamification.quest.service.PlayerFulfillMissionsService

open class UtopiaGamificationListener(
    private val guild: Guild,
    protected val playerRepository: PlayerRepository,
) : UtopiaListener() {

    protected fun User.toPlayer(): Player? {
        if (isBot) {
            return null
        }

        return guild.retrieveMemberById(id).complete()?.toPlayer()
    }

    private fun Member.toPlayer(): Player =
        (playerRepository.findPlayerById(id) ?: playerRepository.savePlayer(
            Player(
                id,
                user.effectiveName,
                joinDate = timeJoined
            )
        )).refresh(this)

    private fun Player.refresh(member: Member): Player {
        jdaRoles.addAll(member.roles.map { it.id }.toMutableList())
        return this
    }

    protected val User.claimMissionRewardPresenter
        get() = object : PlayerFulfillMissionsService.Presenter {
            override fun presentClaimMissionReward(mission: Mission) {
                openPrivateChannel().queue {
                    it.sendMessage(mission.postMessage)
                        .addActionRow(mission.rewardButton)
                        .queue()
                }
            }
        }

    private val Mission.postMessage
        get() = quest.postMessage

    val Mission.rewardButton: Button
        get() = button(
            RewardButton.id(quest.id),
            RewardButton.LABEL
        )
}
