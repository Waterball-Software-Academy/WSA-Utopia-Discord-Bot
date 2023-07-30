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
