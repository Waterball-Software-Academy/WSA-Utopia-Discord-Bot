package tw.waterballsa.alpha.wsabot.bot.app

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.core.event.message.ReactionAddEvent
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.dsl.listeners
import mu.KotlinLogging
import tw.waterballsa.alpha.wsabot.bot.common.GuildEnvironment

val logger = KotlinLogging.logger {}

fun reactionRole() = listeners {

    on<ReactionAddEvent> {
        if (!matchEmoji(emoji.name)) {
            return@on
        }

        if (!matchEntryMessage(messageId)) {
            return@on
        }

        val guildId = message.asMessage().getGuild().id
        val user = user.asUser()
        val givenRoles = givenRoles(guildId)
        val removeRoles = removeRoles(guildId)
        addRoleToGuildMember(discord, guildId, user, givenRoles)
        deleteRoleFromGuildMember(discord, guildId, user, removeRoles)
    }
}

private fun matchEmoji(emojiName: String): Boolean {
    return emojiName == "🔑";
}

private fun matchEntryMessage(messageId: Snowflake): Boolean {
    return listOf(
        Snowflake("1042776102734663740"),
        Snowflake("1038667013259792455")
    ).contains(messageId)
}

private suspend fun addRoleToGuildMember(
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

private suspend fun deleteRoleFromGuildMember(
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

private fun givenRoles(guildId: Snowflake): List<Snowflake> {
    return when (guildId) {
        GuildEnvironment.BETA.guildId
        -> {
            listOf(
                Snowflake("1038661590985228298"), // 學院公民
                Snowflake("1038661795507863623"), // 技術演講吐司會訂閱者
                Snowflake("1038661611889631262")  // 遊戲微服務計畫訂閱者
            )
        }

        GuildEnvironment.PROD.guildId
        -> {
            listOf(
                Snowflake("954669665588756480"), // 學院公民
                Snowflake("1038933719723020318"), // 純函式咖啡訂閱者
                Snowflake("1042774972717871176"), // 遊戲微服務計畫訂閱者
                Snowflake("1042775110630780958"), // 技術演講吐司會訂閱者
                Snowflake("1056758845264900166"), // 軟體英文派對訂閱者
                Snowflake("1051031301609758752")  // Leetcode 解題大會訂閱者
            )
        }

        else -> emptyList()
    }
}

private fun removeRoles(guildId: Snowflake): List<Snowflake> {
    return when (guildId) {
        GuildEnvironment.BETA.guildId
        -> {
            listOf(
                Snowflake("1040635429541646407") // 學院訪客
            )
        }

        GuildEnvironment.PROD.guildId
        -> {
            listOf(
                Snowflake("1042770137742319636") // 學院訪客
            )
        }

        else -> emptyList()
    }
}
