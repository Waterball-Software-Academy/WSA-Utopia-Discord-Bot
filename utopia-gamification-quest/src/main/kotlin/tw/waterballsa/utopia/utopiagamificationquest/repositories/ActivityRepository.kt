package tw.waterballsa.utopia.utopiagamificationquest.repositories

import tw.waterballsa.utopia.utopiagamificationquest.domain.Activity

interface ActivityRepository {

    fun findInProgressActivitiesByChannelId(id: String): Activity?
    fun findAudienceStayedActivity(channelId: String, audienceId: String): Activity?
    fun findByActivityId(id: String): Activity?
    fun save(activity: Activity): Activity
}
