package tw.waterballsa.alpha.wsabot.bot.common

import dev.kord.common.entity.Snowflake

enum class GuildEnvironment(val guildId: Snowflake) {
    BETA(Snowflake("1038654765896310804")),
    PROD(Snowflake("937992003415838761"))
}
