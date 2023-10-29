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
    val link: String = "",
    val nextQuestId: Int? = null,
    val postMessage: String = completeMessage
)

class Reward(
    val exp: ULong,
    // TODO:未來實作商店功能使用金幣兌換道具(職涯攻略、範例程式碼)
    val coin: ULong,
    val bonus: Float,
    val role: RoleType?
) {

    constructor(exp: ULong, coin: ULong, bonus: Float) : this(exp, coin, bonus, null)
    constructor(exp: ULong, role: RoleType) : this(exp, 0uL, 0f, role)

    fun reward(player: Player) {
        player.gainExp(exp)
        player.addRole(role!!.name)
    }
}

// TODO add description
enum class RoleType(
    val description: String,
    val level: Int
) {
    LONG_ARTICLE("長文成就", 0),
    TOPIC_MASTER("話題高手", 0),
    EVERYONE("學院公民", 0),
    WSA_MEMBER("水球成員", 1),
    GENTLEMAN("學院紳士", 2),
    SENIOR_GENTLEMAN("資深紳士", 3),
    MENTOR("學院導師", 3);

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
