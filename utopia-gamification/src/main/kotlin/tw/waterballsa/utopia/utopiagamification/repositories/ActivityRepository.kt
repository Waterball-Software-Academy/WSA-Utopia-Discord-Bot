package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.utopiagamification.activity.domain.Activity

interface ActivityRepository {

    fun findInProgressActivityByChannelId(id: String): Activity?
    fun findAudienceStayActivity(channelId: String, audienceId: String): Activity?
    fun findByActivityId(id: String): Activity?
    fun save(activity: Activity): Activity
}
