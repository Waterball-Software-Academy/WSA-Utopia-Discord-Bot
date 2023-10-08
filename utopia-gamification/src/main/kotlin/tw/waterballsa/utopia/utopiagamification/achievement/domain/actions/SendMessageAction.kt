package tw.waterballsa.utopia.utopiagamification.achievement.domain.actions

import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Type.TEXT_MESSAGE
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player

open class SendMessageAction(
    player: Player,
    private val words: String,
) : Action(TEXT_MESSAGE, player) {

    fun contentWordRequirement(wordLength: Int): Boolean = words.length >= wordLength
}
