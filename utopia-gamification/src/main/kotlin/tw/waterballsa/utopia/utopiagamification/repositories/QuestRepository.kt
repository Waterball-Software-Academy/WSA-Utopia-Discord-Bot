package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest

interface QuestRepository {
    
    fun findById(id: Int): Quest?
    fun save(quest: Quest): Quest
}
