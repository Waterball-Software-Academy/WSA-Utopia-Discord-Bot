package tw.waterballsa.utopia.weeklymessagesvolume

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.extensions.addRequiredOption
import tw.waterballsa.utopia.jda.extensions.getOptionAsStringWithValidation
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import tw.waterballsa.utopia.weeklymessagesvolume.doamin.WeeklyRange

@Component
class WeeklyMessagesVolumeListener : UtopiaListener() {

    companion object {
        private const val WEEKLY_MESSAGE_VOLUME = "weekly-messages-volume"
        private const val CHANNEL_NAME = "channel-name"
    }

    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(WEEKLY_MESSAGE_VOLUME, "Show the weekly messages volume of the channel")
                .addRequiredOption(OptionType.STRING, CHANNEL_NAME, "The channel to show the weekly messages volume")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != WEEKLY_MESSAGE_VOLUME) {
                return
            }

            val channelName = channelName ?: return
            val textChannel = findTextChannelLikeName(channelName) ?: run {
                replyEphemerally("cannot find the channel named $channelName.")
                return
            }

            val messagesVolume = textChannel.countWeeklyMessages()

            replyEphemerally("The weekly messages volume of the channel ${textChannel.name} is $messagesVolume messages.")
        }
    }

    private val SlashCommandInteractionEvent.channelName: String?
        get() = getOptionAsStringWithValidation(CHANNEL_NAME, "channel name is invalid.") { it.isNotBlank() }

    private fun SlashCommandInteractionEvent.findTextChannelLikeName(channelName: String): TextChannel? =
        guild?.textChannels?.find { it.name.contains(channelName, ignoreCase = true) }
}

private fun TextChannel.countWeeklyMessages(): Int =
    iterableHistory.takeWhile { WeeklyRange().contains(it.timeCreated) }.count()
