package tw.waterballsa.utopia.utopiagamificationquest.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection
import tw.waterballsa.utopia.utopiagamificationquest.domain.Activity
import tw.waterballsa.utopia.utopiagamificationquest.domain.Audience
import tw.waterballsa.utopia.utopiagamificationquest.domain.DateTimeRange
import tw.waterballsa.utopia.utopiagamificationquest.extensions.toDate

interface ActivityRepository {
    fun findInProgressActivityByChannelId(id: String): Activity?
    fun findByActivityId(id: String): Activity?
    fun save(activity: Activity): Activity
}

@Component
class MongodbActivityRepository(
    private val repository: MongoCollection<ActivityDocument, String>
) : ActivityRepository {

    //TODO 學會怎麼用 Query 之後，改 Query。
    override fun findInProgressActivityByChannelId(id: String): Activity? =
        repository.findAll().find { it.channelId == id && it.inProgress() }?.toDomain()

    override fun findByActivityId(id: String): Activity? = repository.findOne(id)?.toDomain()

    override fun save(activity: Activity): Activity {
        repository.save(activity.toDocument())
        return activity
    }
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
) {

    fun toDomain(): Activity = Activity(
        id,
        hostId,
        eventName,
        channelId,
        DateTimeRange(startTime.toDate(), endTime.toDate()),
        audiences.associate { it.toDomain() }.toMutableMap()
    )

    fun inProgress(): Boolean = DateTimeRange(startTime.toDate(), endTime.toDate()).inTimeRange()
}

class AudienceDocument(
    val id: String,
    val audienceName: String,
    val startTime: String,
    val endTime: String,
) {

    fun toDomain(): Pair<String, Audience> = id to Audience(
        id,
        audienceName,
        DateTimeRange(startTime.toDate(), endTime.toDate())
    )
}
