package tw.waterballsa.utopia.gaas.extensions

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

internal fun Member.isGaaSMember(gaaSMemberRoleId: String): Boolean =
    gaaSMemberRoleId in roles.mapNotNull { it.id }

internal fun Member.isAlphaMember(alphaRoleId: String): Boolean =
    alphaRoleId in roles.mapNotNull { it.id }

internal fun IReplyCallback.replyEphemerally(message: String) =
    reply(message).setEphemeral(true).queue()

internal fun SubcommandData.addRequiredOption(type: OptionType, name: String, description: String) =
    addOption(type, name, description, true)
