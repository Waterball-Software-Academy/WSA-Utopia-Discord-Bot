package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

class SendMessageMission(
    val gentlemanId : String,
    val channelId: String,
    val hasImage: Boolean,
    val isTag: Boolean,
    val wordLength: Int,
    val publishedCount: Int,
) {

    private lateinit var reward : Reward
}
