package tw.waterballsa.utopia.utopiagamificationquest.repositories.MongoRepositoryImpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiagamificationquest.domain.Activity
import tw.waterballsa.utopia.utopiagamificationquest.domain.Audience
import tw.waterballsa.utopia.utopiagamificationquest.domain.DateTimeRange
import tw.waterballsa.utopia.utopiagamificationquest.extensions.toDate
import tw.waterballsa.utopia.utopiagamificationquest.repositories.ActivityRepository


@Component
class MongodbActivityRepository(
    private val repository: MongoCollection<ActivityDocument, String>
) : ActivityRepository {

    //TODO 學會怎麼用 Query 之後，改 Query。
    override fun findInProgressActivityByChannelId(id: String): Activity? =
        repository.findAll().find { it.channelId == id && it.inProgress() }?.toDomain()

    override fun findByActivityId(id: String): Activity? = repository.findOne(id)?.toDomain()

    override fun save(activity: Activity): Activity = repository.save(activity.toDocument()).toDomain()

    private fun ActivityDocument.inProgress(): Boolean =
        DateTimeRange(startTime.toDate(), endTime.toDate()).inTimeRange()

    private fun ActivityDocument.toDomain(): Activity = Activity(
        id,
        hostId,
        eventName,
        channelId,
        DateTimeRange(startTime.toDate(), endTime.toDate()),
        audiences.associate { it.toDomain() }.toMutableMap()
    )

    private fun AudienceDocument.toDomain(): Pair<String, Audience> = id to Audience(
        id,
        DateTimeRange(startTime.toDate(), endTime.toDate())
    )

    private fun Activity.toDocument(): ActivityDocument = ActivityDocument(
        eventId,
        hostId,
        eventName,
        channelId,
        dateTimeRange.getStartTime(),
        dateTimeRange.getEndTime(),
        audiences.values.map { it.toDocument() }
    )

    private fun Audience.toDocument(): AudienceDocument =
        AudienceDocument(id, joinTime.getStartTime(), joinTime.getEndTime())
}

@Document
class ActivityDocument(
    @Id val id: String,
    val hostId: String,
    val eventName: String,
    val channelId: String,
    val startTime: String,
    val endTime: String,
    val audiences: List<AudienceDocument>
)

class AudienceDocument(
    val id: String,
    val startTime: String,
    val endTime: String,
)
