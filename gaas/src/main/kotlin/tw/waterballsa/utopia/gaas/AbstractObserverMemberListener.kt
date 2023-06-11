package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.gaas.extensions.isAlphaMember
import tw.waterballsa.utopia.gaas.extensions.isGaaSMember
import tw.waterballsa.utopia.gaas.extensions.replyEphemerally
import tw.waterballsa.utopia.jda.UtopiaListener


abstract class AbstractObserverMemberListener(
    private val properties: WsaDiscordProperties,
    protected val observedMemberRepository: ObservedMemberRepository
) : UtopiaListener() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val alphaRoleId = properties.wsaAlphaRoleId
        val gaaSMemberRoleId = properties.wsaGaaSMemberRoleId
        with(event) {
            val commandUser = member!!

            when {
                interaction.fullCommandName != getRegisteredCommandName() -> return
                !commandUser.isAlphaMember(alphaRoleId) -> {
                    replyEphemerally("權限不足")
                    return
                }

                !targetUser.isGaaSMember(gaaSMemberRoleId) -> {
                    replyEphemerally("${targetUser.asMention} 並非 GaaS 成員")
                    return
                }

                !meetObserveCommandCondition() -> {
                    replyHint()
                    return
                }
            }

            executeObserveCommand()
        }
    }
    protected abstract fun getRegisteredCommandName(): String

    protected open fun SlashCommandInteractionEvent.replyHint() {}

    protected abstract fun SlashCommandInteractionEvent.meetObserveCommandCondition(): Boolean

    protected abstract fun SlashCommandInteractionEvent.executeObserveCommand()
}
val SlashCommandInteractionEvent.targetUser: Member
    get() = getOptionsByType(OptionType.USER).first().asMember!!

internal fun Member.toObservedMemberRecord() =
    ObservedMemberRecord(id, nickname ?: effectiveName)

