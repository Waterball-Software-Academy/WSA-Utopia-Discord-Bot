package tw.waterballsa.utopia.utopiagamificationquest.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.*
import tw.waterballsa.utopia.utopiagamificationquest.domain.AudienceState.*
import tw.waterballsa.utopia.utopiagamificationquest.extensions.toDate
import tw.waterballsa.utopia.utopiagamificationquest.repositories.ActivityRepository


@Component
class MongodbActivityRepository(
    private val repository: MongoCollection<ActivityDocument, String>
) : ActivityRepository {

    override fun findInProgressActivitiesByChannelId(id: String): Activity? {
        return repository.find(
            Query(
                Criteria("channelId").`is`(id).and("state").`is`(ActivityState.ACTIVE)
            )
        ).firstOrNull()?.toDomain()
    }

    override fun findAudienceStayedActivity(channelId: String, audienceId: String): Activity? {
        return repository.find(
            Query(
                Criteria("channelId").`is`(channelId)
                    .and("audiences.id").`is`(audienceId)
                    .and("audiences.state").`is`(STAY)
            )
        ).firstOrNull()?.toDomain()
    }

    override fun findByActivityId(id: String): Activity? = repository.findOne(id)?.toDomain()

    override fun save(activity: Activity): Activity = repository.save(activity.toDocument()).toDomain()

    private fun ActivityDocument.toDomain(): Activity = Activity(
        id,
        hostId,
        eventName,
        channelId,
        state,
        DateTimeRange(startTime.toDate(), endTime.toDate()),
        audiences.associate { it.toDomain() }.toMutableMap()
    )

    private fun AudienceDocument.toDomain(): Pair<String, Audience> = id to Audience(
        id,
        state,
        DateTimeRange(startTime.toDate(), endTime.toDate())
    )

    private fun Activity.toDocument(): ActivityDocument = ActivityDocument(
        eventId,
        hostId,
        eventName,
        channelId,
        state,
        dateTimeRange.getStartTime(),
        dateTimeRange.getEndTime(),
        audiences.values.map { it.toDocument() }
    )

    private fun Audience.toDocument(): AudienceDocument =
        AudienceDocument(id, state, joinTime.getStartTime(), joinTime.getEndTime())
}

@Document
class ActivityDocument(
    @Id val id: String,
    val hostId: String,
    val eventName: String,
    val channelId: String,
    val state: ActivityState,
    val startTime: String,
    val endTime: String,
    val audiences: List<AudienceDocument>
)

class AudienceDocument(
    val id: String,
    val state: AudienceState,
    val startTime: String,
    val endTime: String,
)
