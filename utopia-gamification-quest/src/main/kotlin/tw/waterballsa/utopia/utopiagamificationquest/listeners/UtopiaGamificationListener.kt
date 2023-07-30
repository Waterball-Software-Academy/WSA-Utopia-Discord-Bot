package tw.waterballsa.utopia.utopiagamificationquest.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.domain.Player
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService

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

    protected val User.presenter
        get() = object : PlayerFulfillMissionsService.Presenter {
            override fun presentClaimMissionReward(mission: Mission) {
                claimMissionReward(mission)
            }
        }
}
