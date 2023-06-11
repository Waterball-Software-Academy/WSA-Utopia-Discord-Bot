package tw.waterballsa.utopia.gaas

import dev.minn.jda.ktx.generics.getChannel
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.createFileWithFileName
import tw.waterballsa.utopia.gaas.extensions.isGaaSMember
import tw.waterballsa.utopia.gaas.extensions.replyEphemerally
import tw.waterballsa.utopia.jda.UtopiaListener
import java.lang.System.lineSeparator
import java.nio.file.Files.writeString
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId.systemDefault
import kotlin.io.path.Path

/*
* PersonalLeave is a feature of GaaS that allows members to request time off.
* When a GaaS event is created,
* the bot automatically sends a message to remind members that they can request personal leave through it.
* */

@Component
class PersonalLeave(private val properties: WsaDiscordProperties) : UtopiaListener() {
    companion object {
        private const val customButtonId = "gaas-leave"
        private const val customModalId = "leave-modal"
        private const val DATABASE_DIRECTORY = "data/gaas/leave"
        private const val DATABASE_FILENAME_PREFIX = "GaaS-leave"
        private lateinit var eventTime: LocalDateTime
        private val eventNameKeywords = listOf("遊戲微服務", "讀書會")
    }

    override fun onScheduledEventCreate(event: ScheduledEventCreateEvent) {
        with(event) {
            val partyChannelId = properties.wsaPartyChannelId
            val guildId = properties.guildId
            val gaaSConversationChannelId = properties.wsaGaaSConversationChannelId
            val wsaGaaSMemberRoleId = properties.wsaGaaSMemberRoleId
            eventTime = scheduledEvent.startTime.atZoneSameInstant(systemDefault()).toLocalDateTime()
            val conversationChannel = jda.getGuildById(guildId)!!.getChannel<TextChannel>(gaaSConversationChannelId)!!

            scheduledEvent
                .takeIf { it.isGaaSEvent() && it.channel?.id == partyChannelId }
                ?.run {
                    conversationChannel
                        .sendMessage("<@&$wsaGaaSMemberRoleId>\n哈囉各位讀書會夥伴！如果不能參加 **[${eventTime.toLocalDate()}]** 讀書會的夥伴，請點擊按鈕請假喔")
                        .setAllowedMentions(listOf(MentionType.ROLE))
                        .addActionRow(Button.primary(customButtonId, "我要請假"))
                        .queue()
                }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val wsaGaaSMemberRoleId = properties.wsaGaaSMemberRoleId
            val member = member!!

            when {
                button.id != customButtonId -> return

                !member.isGaaSMember(wsaGaaSMemberRoleId) -> {
                    replyEphemerally("看起來你似乎不是 GaaS 讀書會成員喔，那就不用特別請假啦")
                    return
                }

                now().isAfter(eventTime) -> {
                    replyEphemerally("超過可以請假的時間囉，下次請記得要在活動開始前請假喔")
                    return
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

    override fun onModalInteraction(event: ModalInteractionEvent) {
        event.takeIf { it.modalId == customModalId }
            ?.run {
                val filePath = createLeaveRecordFile()
                val member = interaction.member!!
                val nickname = member.nickname ?: member.user.name
                val leaveReason = getValue("leave-reason")?.asString
                writeString(filePath, "$nickname : $leaveReason${lineSeparator()}", APPEND)
                replyEphemerally("哈囉，$nickname！我們收到你的請假申請囉，期待你下週能來和我們暢聊你的開發成果。")
            }
    }

    private fun ScheduledEvent.isGaaSEvent(): Boolean = eventNameKeywords.all { it in name }

    private fun createLeaveRecordFile(): Path =
        Path(DATABASE_DIRECTORY).createFileWithFileName("$DATABASE_FILENAME_PREFIX-${LocalDate.now()}.db")
}
