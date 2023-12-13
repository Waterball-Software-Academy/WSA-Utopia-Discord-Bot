package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

class SendMessageAction(
     val channelId: String,
     val hasImage: Boolean,
     val player: Player,
     val isTag: Boolean,
     val content:String,
) {

    fun progress(sendMessageMission: SendMessageMission){
        sendMessageMission.progress(this)
    }
}
