package tw.waterballsa.utopia.gamification.repositories.mongodb.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.*
import tw.waterballsa.utopia.gamification.activity.domain.Activity
import tw.waterballsa.utopia.gamification.activity.domain.Activity.State.*
import tw.waterballsa.utopia.gamification.activity.domain.Audience
import tw.waterballsa.utopia.gamification.activity.extensions.DateTimeRange
import tw.waterballsa.utopia.gamification.quest.extensions.toDate
import tw.waterballsa.utopia.gamification.repositories.ActivityRepository


@Component
class MongodbActivityRepository(
    private val repository: MongoCollection<ActivityDocument, String>
) : ActivityRepository {

    override fun findInProgressActivityByChannelId(id: String): Activity? = repository.find(
        Query(
            Criteria("channelId").`is`(id).and("state").`is`(ACTIVE)
        )
    ).firstOrNull()?.toDomain()

    override fun findAudienceStayActivity(channelId: String, audienceId: String): Activity? = repository.find(
        Query(
            Criteria("channelId").`is`(channelId)
                .and("audiences.id").`is`(audienceId)
                .and("audiences.state").`is`(Audience.State.STAY)
        )
    ).firstOrNull()?.toDomain()

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
    val state: Activity.State,
    val startTime: String,
    val endTime: String,
    val audiences: List<AudienceDocument>
)

class AudienceDocument(
    val id: String,
    val state: Audience.State,
    val startTime: String,
    val endTime: String,
)
