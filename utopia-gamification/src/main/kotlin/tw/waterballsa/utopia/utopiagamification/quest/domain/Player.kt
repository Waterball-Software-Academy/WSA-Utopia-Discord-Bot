package tw.waterballsa.utopia.utopiagamification.quest.domain

import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet
import tw.waterballsa.utopia.utopiagamification.quest.extensions.LevelSheet.Companion.calculateLevel
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import kotlin.ULong.Companion.MIN_VALUE

class Player(
    val id: String,
    var name: String,
    var exp: ULong = MIN_VALUE,
    var level: UInt = 1u,
    val joinDate: OffsetDateTime = now(),
    var latestActivateDate: OffsetDateTime = now(),
    var levelUpgradeDate: OffsetDateTime = now(),
    // TODO achievement-system 這邊應該要改成 Role 陣列
    val jdaRoles: MutableList<String> = mutableListOf(),
) {

    init {
        calculateLevel()
    }

    val currentLevelExpLimit
        get() = LevelSheet.getLevelRange(level.toInt()).expLimit

    fun gainExp(rewardExp: ULong) {
        exp += rewardExp
        calculateLevel()
        activate()
    }

    fun hasRole(role: RoleType): Boolean = hasRole(role.name)

    fun hasRole(role: String): Boolean = jdaRoles.contains(role)

    fun addRole(role: String){
        jdaRoles.add(role)
    }

    private fun calculateLevel() {
        val newLevel = calculateLevel(exp)
        if (newLevel > level) {
            level = newLevel
            levelUpgradeDate = now()
        }
    }

    private fun activate() {
        latestActivateDate = now()
    }
}
