package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.commons.extensions.onStart
import tw.waterballsa.utopia.commons.extensions.toDate
import tw.waterballsa.utopia.jda.listener
import java.time.LocalDate.now
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.hours

private val timer = Timer()
private val log = logger
private val observedMemberRepository = ObservedMemberRepository()

fun observeMember(properties: WsaDiscordProperties) = listener {
    on<SlashCommandInteractionEvent> {
        val targetUser = getOptionsByType(OptionType.USER).first().asMember!!
        val alphaRoleId = properties.wsaAlphaRoleId
        val commandUser = member!!
        val gaaSMemberRoleId = properties.wsaGaaSMemberRoleId

        when {
            interaction.fullCommandName != "gaas observe" -> return@on
            !commandUser.isAlphaMember(alphaRoleId) -> {
                replyEphemerally("權限不足")
                return@on
            }

            !targetUser.isGaaSMember(gaaSMemberRoleId) -> {
                replyEphemerally("${targetUser.asMention} 並非 GaaS 成員")
                return@on
            }
        }

        if (observedMemberRepository.exists(targetUser.id)) {
            replyEphemerally("${targetUser.asMention} 已經在觀察名單")
            return@on
        }

        observedMemberRepository.addObservedMember(targetUser.toObservedMemberRecord())
        replyEphemerally("${targetUser.asMention} 已經成功加入觀察名單")
    }
}


fun removeMember(properties: WsaDiscordProperties) = listener {
    on<SlashCommandInteractionEvent> {
        val targetUser = getOptionsByType(OptionType.USER).first().asMember!!
        val alphaRoleId = properties.wsaAlphaRoleId
        val commandUser = member!!
        val gaaSMemberRoleId = properties.wsaGaaSMemberRoleId

        when {
            interaction.fullCommandName != "gaas unobserve" -> return@on
            !commandUser.isAlphaMember(alphaRoleId) -> {
                replyEphemerally("權限不足")
                return@on
            }

            !targetUser.isGaaSMember(gaaSMemberRoleId) -> {
                replyEphemerally("${targetUser.asMention} 並非 GaaS 成員")
                return@on
            }
        }

        if (observedMemberRepository.exists(targetUser.id)) {
            observedMemberRepository.removeObservedMember(targetUser.id)
        }
        replyEphemerally("${targetUser.asMention} 已經從觀察名單移除")
    }
}

fun removeExpiredMembersPeriodically(jda: JDA, properties: WsaDiscordProperties) = listener {
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
