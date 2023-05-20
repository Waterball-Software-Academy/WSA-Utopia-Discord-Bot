package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.UtopiaListener
import java.util.*
import kotlin.concurrent.timerTask
import java.time.LocalDate.now
import kotlin.time.Duration.Companion.hours

@Component
class ExpiredMemberListener(
    private val observedMemberRepository: ObservedMemberRepository,
    private val jda: JDA,
    private val properties: WsaDiscordProperties
) : UtopiaListener() {
    private val timer = Timer()
    private val log = logger

    init {
        removeExpiredMembersPeriodically()
    }

    private fun removeExpiredMembersPeriodically()  {
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
    ) = timerTask {
        observedMemberRepository.findAll()
            .filter { it.isCreatedTimeOver30Days() }
            .takeIf { it.isNotEmpty() }
            ?.onEach { removeRoleFromRecord(guild, it, role) }
            ?.map { it.id }
            ?.let { observedMemberRepository.removeObservedMemberByIds(it) }
    }

    private fun removeRoleFromRecord(guild: Guild, record: ObservedMemberRecord, role: Role) {
        guild.removeRoleFromMember(UserSnowflake.fromId(record.id), role).queue {
            log.info { "[Observe] {\"Observe\" : \"Remove the expired Member [${record.name}]. \"}" }
        }
    }
}

