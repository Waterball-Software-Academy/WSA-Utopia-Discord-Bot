package tw.waterballsa.utopia.utopiagamification.quest.domain


import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.Companion.toLevel
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

class Player(
    val id: String,
    var name: String,
    exp: ULong = 0uL,
    level: UInt = 1u,
    var bounty: UInt = 0u,
    val joinDate: OffsetDateTime = now(),
    latestActivateDate: OffsetDateTime = now(),
    levelUpgradeDate: OffsetDateTime? = null,
    val jdaRoles: MutableList<String> = mutableListOf()
) {

    var exp = exp
        private set

    var level = level
        private set

    var levelUpgradeDate = levelUpgradeDate
        private set

    var latestActivateDate = latestActivateDate
        private set

    init {
        this.level = exp.toLevel()
    }

    fun gainExp(rewardExp: ULong) {
        exp += rewardExp
        val newLevel = exp.toLevel()
        if (newLevel != level) {
            level = newLevel
            levelUpgradeDate = now()
        }
        activate()
    }

    fun hasRole(role: RoleType): Boolean = hasRole(role.name)

    fun hasRole(role: String): Boolean = jdaRoles.contains(role)

    fun addRole(role: String){
        jdaRoles.add(role)
    }

    private fun activate() {
        latestActivateDate = now()
    }
}
