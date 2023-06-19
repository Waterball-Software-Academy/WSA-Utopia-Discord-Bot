package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.gaas.extensions.replyEphemerally

@Component
class UnobservedMemberLister(
    properties: WsaDiscordProperties,
    observedMemberRepository: ObservedMemberRepository
) : AbstractObserverMemberListener(properties, observedMemberRepository) {

    override fun getRegisteredCommandName(): String = "gaas unobserved"

    override fun SlashCommandInteractionEvent.meetObserveCommandCondition(): Boolean = observedMemberRepository.exists(targetUser.id)

    override fun SlashCommandInteractionEvent.executeObserveCommand() {
        observedMemberRepository.removeObservedMember(targetUser.id)
        replyEphemerally("${targetUser.asMention} 已經從觀察名單移除")
    }
}
