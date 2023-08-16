package tw.waterballsa.utopia.utopiagamificationquest.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ScheduledEvent.Status
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.GenericScheduledEventUpdateEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamificationquest.domain.Activity
import tw.waterballsa.utopia.utopiagamificationquest.domain.ActivityState
import tw.waterballsa.utopia.utopiagamificationquest.domain.DateTimeRange
import tw.waterballsa.utopia.utopiagamificationquest.repositories.ActivityRepository
import tw.waterballsa.utopia.utopiagamificationquest.repositories.PlayerRepository
import tw.waterballsa.utopia.utopiagamificationquest.service.PlayerFulfillMissionsService
import java.time.LocalDateTime.now
import java.time.ZoneId

private val log = KotlinLogging.logger {}

@Component
class EventJoiningListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsService: PlayerFulfillMissionsService,
    private val activityRepository: ActivityRepository
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        with(event) {
            val user = member.user
            val player = user.toPlayer() ?: return

            channelJoined?.let {
                val inProgressActivity = activityRepository.findInProgressActivitiesByChannelId(it.id) ?: return
                inProgressActivity.join(player)
                activityRepository.save(inProgressActivity)
            }

            channelLeft?.let {
                val stayedActivity = activityRepository.findAudienceStayedActivity(it.id, player.id) ?: return
                val action = stayedActivity.leave(player) ?: return
                playerFulfillMissionsService.execute(action, user.claimMissionRewardPresenter)
                activityRepository.save(stayedActivity)
            }
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
                    status.toActivityState(),
                    DateTimeRange(startTime.atZoneSameInstant(ZoneId.of("Asia/Taipei")).toLocalDateTime())
                )
            )
            log.info("""[activity created] "activityId" = "$id", "activityName" = "$name"} """)
        }

    private fun Status.toActivityState(): ActivityState {
        return when (this) {
            Status.SCHEDULED -> ActivityState.SCHEDULED
            Status.ACTIVE -> ActivityState.ACTIVE
            Status.COMPLETED -> ActivityState.COMPLETED
            else -> ActivityState.CANCELED
        }
    }

    override fun onScheduledEventDelete(event: ScheduledEventDeleteEvent) {
        with(event.scheduledEvent) {
            val activity = activityRepository.findByActivityId(id) ?: return
            activity.cancel()
            log.info("""[activity canceled] "activityId" = "$id", "activityName" = "$name"} """)
            activityRepository.save(activity)
        }
    }

    override fun onGenericScheduledEventUpdate(event: GenericScheduledEventUpdateEvent<*>) {
        with(event.scheduledEvent) {
            val startTime = startTime.atZoneSameInstant(ZoneId.of("Asia/Taipei")).toLocalDateTime()
            val endTime = if (status == Status.COMPLETED) now() else startTime

            val activity = Activity(
                id,
                creatorIdLong.toString(),
                name,
                location,
                status.toActivityState(),
                DateTimeRange(startTime, endTime),
                activityRepository.findByActivityId(id)?.audiences ?: mutableMapOf()
            )

            activityRepository.save(activity)

            log.info("""[activity updated] "activityId" = "$id", "activityName" = "$name"} """)
        }
    }
}
