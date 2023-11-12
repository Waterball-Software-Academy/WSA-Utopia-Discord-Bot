package tw.waterballsa.utopia.utopiagamification.activity.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ScheduledEvent.Status
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent
import net.dv8tion.jda.api.events.guild.scheduledevent.update.GenericScheduledEventUpdateEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.activity.domain.Activity
import tw.waterballsa.utopia.utopiagamification.activity.domain.Activity.State.*
import tw.waterballsa.utopia.utopiagamification.activity.extensions.DateTimeRange
import tw.waterballsa.utopia.utopiagamification.quest.extensions.publishToUser
import tw.waterballsa.utopia.utopiagamification.quest.extensions.toTaipeiLocalDateTime
import tw.waterballsa.utopia.utopiagamification.quest.listeners.UtopiaGamificationListener
import tw.waterballsa.utopia.utopiagamification.quest.listeners.presenters.PlayerFulfillMissionPresenter
import tw.waterballsa.utopia.utopiagamification.quest.usecase.PlayerFulfillMissionsUsecase
import tw.waterballsa.utopia.utopiagamification.repositories.ActivityRepository
import tw.waterballsa.utopia.utopiagamification.repositories.PlayerRepository
import java.time.LocalDateTime.now

private val log = KotlinLogging.logger {}

@Component
class EventJoiningListener(
    guild: Guild,
    playerRepository: PlayerRepository,
    private val playerFulfillMissionsUsecase: PlayerFulfillMissionsUsecase,
    private val activityRepository: ActivityRepository
) : UtopiaGamificationListener(guild, playerRepository) {

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        with(event) {
            val user = member.user
            val player = user.toPlayer() ?: return

            channelJoined?.let {
                val inProgressActivity = activityRepository.findInProgressActivityByChannelId(it.id) ?: return@let
                inProgressActivity.join(player)
                activityRepository.save(inProgressActivity)
            }

            channelLeft?.let {
                val stayActivity = activityRepository.findAudienceStayActivity(it.id, player.id) ?: return@let
                val action = stayActivity.leave(user.id) ?: return
                val presenter = PlayerFulfillMissionPresenter()
                playerFulfillMissionsUsecase.execute(action, presenter)
                activityRepository.save(stayActivity)
                presenter.viewModel?.publishToUser(user)
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
                    DateTimeRange(startTime.toTaipeiLocalDateTime())
                )
            )
            log.info("""[activity created] "activityId" = "$id", "activityName" = "$name"} """)
        }

    private fun Status.toActivityState(): Activity.State {
        return when (this) {
            Status.SCHEDULED -> SCHEDULED
            Status.ACTIVE -> ACTIVE
            Status.COMPLETED -> COMPLETED
            else -> CANCELED
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
            val startTime = startTime.toTaipeiLocalDateTime()
            val endTime = if (status == Status.COMPLETED) now() else startTime

            activityRepository.save(
                Activity(
                    id,
                    creatorIdLong.toString(),
                    name,
                    location,
                    status.toActivityState(),
                    DateTimeRange(startTime, endTime),
                    activityRepository.findByActivityId(id)?.audiences ?: mutableMapOf()
                )
            )

            log.info("""[activity updated] "activityId" = "$id", "activityName" = "$name"} """)
        }
    }
}
