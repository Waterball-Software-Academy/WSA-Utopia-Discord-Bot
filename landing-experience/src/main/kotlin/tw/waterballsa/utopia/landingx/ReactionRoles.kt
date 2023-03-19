package tw.waterballsa.utopia.landingx

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.core.event.message.ReactionAddEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties

val logger = KotlinLogging.logger {}

fun reactKeyEmojiToMessageToGetRoles(wsa: WsaDiscordProperties) = listeners {
    on<ReactionAddEvent> {
        if (!matchEmoji(emoji.name)) {
            return@on
        }

        if (wsa.unlockEntryMessageId != messageId.value) {
            return@on
        }

        val guildId = message.asMessage().getGuild().id
        val user = user.asUser()
        val subscriptionRoleIds = findAllSubscriptionRoleIds(wsa, discord)
        addRolesToGuildMember(discord, guildId, user, subscriptionRoleIds.plus(Snowflake(wsa.wsaCitizenRoleId)))
        deleteRolesFromGuildMember(discord, guildId, user, listOf(Snowflake(wsa.wsaGuestRoleId)))
    }
}
suspend fun findAllSubscriptionRoleIds(wsa: WsaDiscordProperties, discord: Discord): List<Snowflake> {
    val guild = discord.kord.getGuildOrNull(Snowflake(wsa.guildId))
    return guild!!.roles.filter { it.name.contains("訂閱") }
            .map { it.id }.toList()
}

private fun matchEmoji(emojiName: String): Boolean {
    return emojiName == "🔑"
}

private suspend fun addRolesToGuildMember(
        discord: Discord,
        guildId: Snowflake,
        user: User,
        roles: List<Snowflake>
) {
    if (roles.isEmpty()) {
        return
    }
    roles.forEach { role ->
        discord.kord.rest.guild.addRoleToGuildMember(guildId, user.id, role)
    }
    logger.info { "${user.username} add roles $roles" }
}

private suspend fun deleteRolesFromGuildMember(
        discord: Discord,
        guildId: Snowflake,
        user: User,
        roles: List<Snowflake>
) {
    if (roles.isEmpty()) {
        return
    }
    roles.forEach { role ->
        discord.kord.rest.guild.deleteRoleFromGuildMember(guildId, user.id, role)
    }
    logger.info { "${user.username} delete roles $roles" }
}