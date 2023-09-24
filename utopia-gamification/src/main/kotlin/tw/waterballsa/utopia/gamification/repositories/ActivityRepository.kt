package tw.waterballsa.utopia.gamification.repositories

import tw.waterballsa.utopia.gamification.activity.domain.Activity

interface ActivityRepository {

    fun findInProgressActivityByChannelId(id: String): Activity?
    fun findAudienceStayActivity(channelId: String, audienceId: String): Activity?
    fun findByActivityId(id: String): Activity?
    fun save(activity: Activity): Activity
}
