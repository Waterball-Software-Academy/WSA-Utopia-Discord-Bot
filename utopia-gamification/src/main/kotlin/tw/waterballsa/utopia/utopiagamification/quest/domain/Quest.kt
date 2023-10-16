package tw.waterballsa.utopia.utopiagamification.quest.domain

private const val completeMessage = "任務完成！"

class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val preCondition: PreCondition = EmptyPreCondition(),
    val roleType: RoleType = RoleType.EVERYONE,
    val periodType: PeriodType = PeriodType.NONE,
    val criteria: Action.Criteria,
    val reward: Reward,
    val nextQuestId: Int? = null,
    val postMessage: String = completeMessage
)

class Reward(
    val exp: ULong,
    //TODO:未來實作商店功能使用金幣兌換道具(職涯攻略、範例程式碼)
    val coin: ULong,
    val bonus: Float,
    val role: RoleType?
) {

    constructor(exp: ULong, coin: ULong, bonus: Float) : this(exp, coin, bonus, null)
}

enum class RoleType(
    val level: Int
) {
    EVERYONE(0),
    WSA_MEMBER(1),
    GENTLEMAN(2),
    SENIOR_GENTLEMAN(3),
    MENTOR(3);

    fun isHigherThanRoleLevel(level: Int): Boolean = level >= this.level
}

enum class PeriodType {
    NONE,
    MAIN_QUEST,
    ACHIEVEMENT,
    ACTIVITY,
    DAILY,
    WEEKLY
}

interface PreCondition {

    fun meet(condition: Condition): Boolean
}

class QuestIdPreCondition(
    private val questId: Int
) : PreCondition {

    override fun meet(condition: Condition): Boolean = condition.quest.id == questId
}

class QuestRoleTypePreCondition(
    private val roleType: RoleType
) : PreCondition {

    override fun meet(condition: Condition): Boolean = condition.quest.roleType.isHigherThanRoleLevel(roleType.level)
}

class PlayerLevelPreCondition(
    private val level: UInt
) : PreCondition {

    override fun meet(condition: Condition): Boolean = condition.player.level >= level
}

class EmptyPreCondition : PreCondition {

    override fun meet(condition: Condition): Boolean = true
}

data class Condition(
    val player: Player,
    val quest: Quest
)
