package tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements

import tw.waterballsa.utopia.utopiagamification.achievement.domain.actions.Action
import tw.waterballsa.utopia.utopiagamification.achievement.domain.events.AchievementAchievedEvent
import tw.waterballsa.utopia.utopiagamification.quest.domain.Player
import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType
import java.util.UUID.randomUUID

abstract class Achievement(
    val name: Name,
    val type: Type,
    private val condition: Condition,
    private val rule: Rule,
    private val reward: Reward,
) {

    fun progressAction(action: Action, progression: Progression?): Progression =
        progress(action, progression ?: Progression(randomUUID().toString(), action.player.id, name, type))

    /**
     * progress flow
     * 1. The player do an action
     * 2. check this action is meet the condition of achievement, and return the progression
     *    2-1. if meet the condition, return the progression
     *    2-2. if not, return null
     */
    private fun progress(action: Action, progression: Progression): Progression =
        if (condition.meet(action)) progression.refresh() else progression

    /**
     * achieve flow
     * 1. Get the progression from progress flow
     * 2. check this progression is achieved the achievement
     *    and player has not achieved this achievement
     *    2-1. if achieved, reward the player and return the achievement-progressed-event
     *    2-2. if not, return null
     */
    fun achieve(player: Player, progression: Progression): AchievementAchievedEvent? {
        return if (rule.isAchieved(player, progression)) {
            reward.reward(player)
            toAchieveEvent()
        } else null
    }

    protected fun toAchieveEvent(): AchievementAchievedEvent = AchievementAchievedEvent(reward)

    enum class Name {
        LONG_ARTICLE,
        TOPIC_MASTER,
    }

    enum class Type {
        TEXT_MESSAGE
    }

    class Progression(
        val id: String,
        val playerId: String,
        val name: Name,
        val type: Type,
        var count: Int = 0,
    ) {
        fun refresh(): Progression {
            count++
            return this
        }

        fun isAchieved(achievementCount: Int): Boolean = count == achievementCount
    }

    interface Condition {
        fun meet(action: Action): Boolean
    }

    class Rule(
        private val role: RoleType,
        private val achievedCount: Int
    ) {
        fun isAchieved(player: Player, progression: Progression): Boolean =
            !player.hasRole(role.name) && progression.isAchieved(achievedCount)
    }
}
