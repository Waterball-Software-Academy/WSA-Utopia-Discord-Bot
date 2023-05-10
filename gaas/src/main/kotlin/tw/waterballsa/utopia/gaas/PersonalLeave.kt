package tw.waterballsa.utopia.gaas

import dev.minn.jda.ktx.generics.getChannel
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.DeprecatedUtopiaListener
import tw.waterballsa.utopia.jda.listener
import java.lang.System.lineSeparator
import java.nio.file.Files.writeString
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile

/*
* PersonalLeave is a feature of GaaS that allows members to request time off.
* When a GaaS event is created,
* the bot automatically sends a message to remind members that they can request personal leave through it.
* */

private const val customButtonId = "gaas-leave"
private const val customModalId = "leave-modal"
private const val DATABASE_DIRECTORY = "data/gaas/leave"
private const val DATABASE_FILENAME_PREFIX = "GaaS-leave"
private lateinit var eventTime: LocalDateTime

fun createLeaveButtonOnGaaSEventCreated(wsaDiscordProperties: WsaDiscordProperties): DeprecatedUtopiaListener {
    return listener {
        on<ScheduledEventCreateEvent> {
            val partyChannelId = wsaDiscordProperties.wsaPartyChannelId
            val guildId = wsaDiscordProperties.guildId
            val gaaSConversationChannelId = wsaDiscordProperties.wsaGaaSConversationChannelId
            val wsaGaaSMemberRoleId = wsaDiscordProperties.wsaGaaSMemberRoleId
            eventTime = scheduledEvent.startTime.atZoneSameInstant(systemDefault()).toLocalDateTime()
            val conversationChannel = jda.getGuildById(guildId)!!.getChannel<TextChannel>(gaaSConversationChannelId)!!

            scheduledEvent
                    .takeIf { it.name.contains("遊戲微服務") && it.channel?.id == partyChannelId }
                    ?.run {
                        conversationChannel
                                .sendMessage("<@&$wsaGaaSMemberRoleId>\n哈囉各位讀書會夥伴！如果不能參加 **[${eventTime.toLocalDate()}]** 讀書會的夥伴，請點擊按鈕請假喔")
                                .setAllowedMentions(listOf(MentionType.ROLE))
                                .addActionRow(Button.primary(customButtonId, "我要請假"))
                                .queue()
                    }
        }
    }
}

fun replyLeaveModalOnLeaveButtonClicked(wsaDiscordProperties: WsaDiscordProperties) = listener {
    val wsaGaaSMemberRoleId = wsaDiscordProperties.wsaGaaSMemberRoleId
    on<ButtonInteractionEvent> {
        if (button.id != customButtonId) {
            return@on
        }
        when {
            !(member!!.isGaaSMember(wsaGaaSMemberRoleId)) -> {
                reply("看起來你似乎不是 GaaS 讀書會成員喔，那就不用特別請假啦").setEphemeral(true).queue()
                return@on
            }

            LocalDateTime.now().isAfter(eventTime) -> {
                reply("超過可以請假的時間囉，下次請記得要在活動開始前請假喔").setEphemeral(true).queue()
                return@on
            }
        }

        val subject = TextInput.create("leave-reason", "請假事由", TextInputStyle.SHORT)
                .setPlaceholder("Subject of this ticket")
                .setMinLength(10)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build()

        Modal.create(customModalId, "GaaS 讀書會請假條")
                .addActionRow(subject)
                .build()
                .also { replyModal(it).queue() }
    }
}

fun saveLeaveReasonOnSubmitted() = listener {
    on<ModalInteractionEvent> {
        takeIf { modalId == customModalId }
                ?.run {
                    val filePath = createLeaveRecordFile()
                    val member = interaction.member!!
                    val nickname = member.nickname ?: member.user.name
                    val leaveReason = getValue("leave-reason")?.asString
                    writeString(filePath, "$nickname : $leaveReason${lineSeparator()}", APPEND)
                    reply("哈囉，$nickname！我們收到你的請假申請囉，期待你下週能來和我們暢聊你的開發成果。").setEphemeral(true)
                            .queue()
                }
    }
}

private fun createLeaveRecordFile(): Path =
        Path(DATABASE_DIRECTORY)
                .createDirectories()
                .resolve("$DATABASE_FILENAME_PREFIX-${LocalDate.now()}.db")
                .createFile()

