package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

class SendMessageAction(
     val channelId: String,
     val hasImage: Boolean,
     val isTag: Boolean,
     val content:String,
) {

    fun progress(sendMessageMission: SendMessageMission){
        sendMessageMission.progress(this)
    }
}
