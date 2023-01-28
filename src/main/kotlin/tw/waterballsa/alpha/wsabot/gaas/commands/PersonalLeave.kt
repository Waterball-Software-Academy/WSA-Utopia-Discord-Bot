package tw.waterballsa.alpha.wsabot.gaas.commands

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.event.guild.GuildScheduledEventCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import me.jakejmattson.discordkt.dsl.MenuButtonRowBuilder
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.createMenu
import tw.waterballsa.alpha.wsabot.gaas.entities.GaasLeave
import tw.waterballsa.alpha.wsabot.gaas.repositories.GaasLeaveRepository

//val gaasConversationChannelId = 975351568398442507 // WSA - 遊戲微服務交流頻道
val gaasConversationChannelId = Snowflake(1039197324062240820) // beta - 遊戲微服務交流頻道
val gaasRoleId = (1039198138403135538).toULong()
//val guildId = Snowflake(937992003415838761) // WSA server
val guildId = Snowflake(1038654765896310804) // beta server
//        TODO 尚未決定持久化實作框架，未來會賦值實作子類
val gaasLeaveRepository = object : GaasLeaveRepository {
    override fun takeLeave(gaasLeave: GaasLeave) {}
}

fun createAskForLeaveMenu() = listeners {
    on<GuildScheduledEventCreateEvent> {
        val gaasChannel = getGuild().getChannel(gaasConversationChannelId) as TextChannel
        takeIf { scheduledEvent.name.contains("遊戲微服務") }
            ?.run { createLeaveMenu(gaasChannel) }
    }
    on<GuildModalSubmitInteractionCreateEvent> {
        val reason = interaction.textInputs["ask-leave-reason"]?.value ?: ""
        val user = interaction.user.discriminator
        gaasLeaveRepository.takeLeave(GaasLeave(user, reason))
        interaction.respondEphemeral { content = "我們已收到你的請假資訊，期待你下週能來和我們暢聊你的開發成果！" }
    }
}

private suspend fun createLeaveMenu(gaasChannel: TextChannel) =
    gaasChannel.createMenu {
        page {
            title = "請假"
            description = "本次讀書會無法參加者，請點此請假並留下請假事由"
        }
        buttons { createLeaveButton() }
    }

private fun MenuButtonRowBuilder.createLeaveButton() =
    actionButton("申請", null, ButtonStyle.Primary) {
        val isGaasMember = user.asMember(guildId).roleIds.any { it.value == gaasRoleId }
        if (isGaasMember)
            createLeaveModal()
    }


private suspend fun ComponentInteraction.createLeaveModal() =
    modal("GaaS 請假申請單", "ask-leave-form") {
        actionRow {
            textInput(TextInputStyle.Short, "ask-leave-reason", "請假事由")
            build()
        }
    }
