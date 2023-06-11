package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.gaas.extensions.replyEphemerally

@Component
class ObserveMemberListener(
    properties: WsaDiscordProperties,
    observedMemberRepository: ObservedMemberRepository
) : AbstractObserverMemberListener(properties, observedMemberRepository) {

    override fun getRegisteredCommandName(): String = "gaas observe"

    override fun SlashCommandInteractionEvent.meetObserveCommandCondition(): Boolean =
        !observedMemberRepository.exists(targetUser.id)

    override fun SlashCommandInteractionEvent.replyHint() {
        replyEphemerally("${targetUser.asMention} 已經在觀察名單")
    }

    override fun SlashCommandInteractionEvent.executeObserveCommand() {
        observedMemberRepository.addObservedMember(targetUser.toObservedMemberRecord())
        replyEphemerally("${targetUser.asMention} 已經成功加入觀察名單")
    }
}
