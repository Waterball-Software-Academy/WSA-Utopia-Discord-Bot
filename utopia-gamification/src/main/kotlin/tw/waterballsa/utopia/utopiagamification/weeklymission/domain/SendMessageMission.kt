package tw.waterballsa.utopia.utopiagamification.weeklymission.domain

import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward

class SendMessageMission(
    val gentlemanId : String,
    val channelId: String,
    val hasImage: Boolean,
    val isTag: Boolean,
    val wordLength: Int,
    val publishedCount: Int,
    var progressCount: Int = 0,
): WeeklyMission() {

    init {
        var exp = 0uL
        if(hasImage){
            exp +=50uL
        }
        if(isTag){
            exp+=30uL
        }
        exp += when(wordLength){
             in 0..20-> 80uL
             in 21..50-> 120uL
             in 51..100-> 150uL
             in 101..200-> 200uL
             in 201..300-> 280uL
             else-> 360uL
        }
        exp *= when(publishedCount){
            1 -> 10uL
            2 -> 11uL
            3 -> 15uL
            4 -> 20uL
            else -> 27uL
        }
        exp/=10uL
        reward = Reward(exp,0uL,0f)
    }

    fun progress(sendMessageAction: SendMessageAction): CompletedMissionEvent?{
        if(checkAllCondition(sendMessageAction)){
            progressCount++;
        }
        return if (isComplete()){
            return CompletedMissionEvent()
        } else null
    }

    fun checkAllCondition(sendMessageAction: SendMessageAction) : Boolean {
        return with(sendMessageAction){
            isValidChannelId(channelId) &&
                    isSendImage(hasImage) &&
                    isWriteTag(isTag) &&
                    contentWordRequirement(content)
        }
    }

    fun isValidChannelId(channelId: String):Boolean = this.channelId == channelId

    fun isSendImage(hasImage: Boolean):Boolean = this.hasImage == hasImage

    fun isWriteTag(isTag: Boolean): Boolean = this.isTag==isTag

    fun contentWordRequirement(content: String) :Boolean = this.wordLength <= content.length

    fun isComplete() : Boolean {
        if(publishedCount == progressCount) {
            status = Status.COMPLETE
            return true
        }
        return false
    }
}
