package tw.waterballsa.utopia.gaas

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

internal fun Member.isGaaSMember(gaaSMemberRoleId: String): Boolean =
    gaaSMemberRoleId in roles.mapNotNull { it.id }

internal fun Member.isAlphaMember(alphaRoleId: String): Boolean =
    alphaRoleId in roles.mapNotNull { it.id }

internal fun SlashCommandInteractionEvent.replyEphemerally(message: String) =
    reply(message).setEphemeral(true).queue()
