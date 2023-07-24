package tw.waterballsa.utopia.utopiagamificationquest

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.ScheduledEvent.Status
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiagamificationquest.domain.Activity
import tw.waterballsa.utopia.utopiagamificationquest.domain.Audience
import tw.waterballsa.utopia.utopiagamificationquest.domain.DateTimeRange
import tw.waterballsa.utopia.utopiagamificationquest.domain.Mission
import tw.waterballsa.utopia.utopiagamificationquest.extensions.claimMissionReward
import tw.waterballsa.utopia.utopiagamificationquest.repositories.ActivityRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService
import java.time.ZoneId

private val log = KotlinLogging.logger {}

@Component
class EventJoiningListener(
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
    private val activityRepository: ActivityRepository
) : UtopiaListener() {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        with(event) {
            val player = member.user

            channelJoined?.activity?.let {
                it.join(player.toAudience())
                activityRepository.save(it)
            }

            channelLeft?.activity?.let {
                val action = it.leave(player.id) ?: return
                playerFulfillMissionsService.execute(action, player.presenter)
                activityRepository.save(it)
            }
        }
    }

    //TODO 如果 兩個活動 重複了 channel id 該怎麼辦
    private val AudioChannelUnion.activity
        get() = activityRepository.findInProgressActivityByChannelId(id)

    private fun User.toAudience(): Audience = Audience(id, name)

    private val User.presenter
        get() = object : PlayerFulfillMissionsService.Presenter {
            override fun presentClaimMissionReward(mission: Mission) {
                claimMissionReward(mission)
            }
        }

    override fun onScheduledEventCreate(event: ScheduledEventCreateEvent): Unit =
        with(event.scheduledEvent) {
            activityRepository.save(
                Activity(
                    id,
                    creatorIdLong.toString(),
                    name,
                    location,
                    DateTimeRange(startTime.atZoneSameInstant(ZoneId.of("Asia/Taipei")).toLocalDateTime())
                )
            )
            log.info("""[activity created] "activityId" = "$id", "activityName" = "$name"} """)
        }


    override fun onScheduledEventUpdateStatus(event: ScheduledEventUpdateStatusEvent) {
        with(event.scheduledEvent) {
            if (status == Status.COMPLETED) {
                val activity = activityRepository.findByActivityId(id) ?: return
                activity.end()
                activityRepository.save(activity)
                log.info("""[activity end] "activityId" = "$id", "activityName" = "$name"} """)
            }
        }
    }

    override fun onScheduledEventDelete(event: ScheduledEventDeleteEvent) {
        with(event.scheduledEvent) {
            val activity = activityRepository.findByActivityId(id) ?: return
            activity.end()
            activityRepository.save(activity)
            log.info("""[activity end] "activityId" = "$id", "activityName" = "$name"} """)
        }
    }
}
