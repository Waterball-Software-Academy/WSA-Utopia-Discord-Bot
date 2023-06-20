package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.UtopiaListener
import java.time.LocalDate.now
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.hours


private val log = logger

@Component
class ExpiredMemberListener(
    private val observedMemberRepository: ObservedMemberRepository,
    private val jda: JDA,
    private val properties: WsaDiscordProperties,
    private val timer: Timer
) : UtopiaListener() {

    companion object {
        private const val FAMOUS_QUOTES_FROM_XI = "https://tenor.com/view/we-missed-him-xi-jinping-talking-gif-14355481"
    }

    init {
        removeExpiredMembersPeriodically()
    }

    private fun removeExpiredMembersPeriodically() {
        val guild = jda.getGuildById(properties.guildId)!!
        val role = jda.getRoleById(properties.wsaGaaSMemberRoleId)!!

        timer.onStart(
            removeExpiredMemberRolePeriodically(guild, role),
            now().atStartOfDay().toDate(),
            24.hours.inWholeMilliseconds
        )
    }

    private fun removeExpiredMemberRolePeriodically(
        guild: Guild,
        role: Role
    ): TimerTask = timerTask {
        observedMemberRepository.findAll()
            .filter { it.isCreatedTimeOver30Days() }
            .onEach { removeRoleFromRecord(guild, it, role) }
            .map { it.id }
            .let { observedMemberRepository.removeObservedMemberByIds(it) }
    }

    private fun removeRoleFromRecord(guild: Guild, record: ObservedMemberRecord, role: Role) {
        val userId = UserSnowflake.fromId(record.id)
        val removedMember = guild.getMemberById(record.id)!!
        guild.publishRemovalMessage(removedMember)
        guild.removeRoleFromMember(userId, role).queue {
            log.info { "[Observe] {\"Observe\" : \"Remove the expired Member [${record.name}]. \"}" }
        }
    }

    private fun Guild.publishRemovalMessage(removedMember: Member) {
        val gaasChannel = getTextChannelById(properties.wsaGaaSConversationChannelId)!!
        gaasChannel.sendMessage("${removedMember.asMention} 已經離開我們了，我們懷念他。")
        gaasChannel.sendMessage(FAMOUS_QUOTES_FROM_XI)
    }
}
