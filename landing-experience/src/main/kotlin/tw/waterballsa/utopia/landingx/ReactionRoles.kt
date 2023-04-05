package tw.waterballsa.utopia.landingx

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.listener

val logger = KotlinLogging.logger {}

fun reactKeyEmojiToMessageToGetRoles(wsa: WsaDiscordProperties, jda: JDA) = listener {
    on<MessageReactionAddEvent> {
        if (!matchEmoji(emoji.name)) {
            return@on
        }

        if (wsa.unlockEntryMessageId != messageId) {
            return@on
        }

        val guild = jda.getGuildById(wsa.guildId)!!
        val subscriptionRoles = findAllSubscriptionRoleIds(guild)
        val citizenRole = guild.getRoleById(wsa.wsaCitizenRoleId)!!
        addRolesToGuildMember(guild, user!!, subscriptionRoles + citizenRole)
        val guestRole = guild.getRoleById(wsa.wsaGuestRoleId)!!
        deleteRolesFromGuildMember(guild, user!!, listOf(guestRole))
    }
}

fun findAllSubscriptionRoleIds(guild: Guild): List<Role> {
    return guild.roles.filter { it.name.contains("訂閱") }
}

private fun matchEmoji(emojiName: String): Boolean {
    return emojiName == "🔑"
}

private fun addRolesToGuildMember(
        guild: Guild,
        user: User,
        roles: List<Role>
) {
    if (roles.isEmpty()) {
        return
    }

    roles.forEach { role ->
        guild.addRoleToMember(user, role)
                .queue {
                    logger.info { "[Role added] {\"userName\":\"${user.name}\", \"roleId\":\"${role.id}\" }" }
                }
    }
}

private fun deleteRolesFromGuildMember(
        guild: Guild,
        user: User,
        roles: List<Role>
) {
    if (roles.isEmpty()) {
        return
    }
    roles.forEach { role ->
        guild.removeRoleFromMember(user, role)
                .queue {
                    logger.info { "[Role removed] {\"userName\":\"${user.name}\", \"roleId\":\"${role.id}\" }" }
                }
    }
}
