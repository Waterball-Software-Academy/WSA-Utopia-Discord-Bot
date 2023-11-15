package tw.waterballsa.utopia.utopiagamification.quest.domain.exception

class AssignedQuestException(playerId: String, questId: Int) :
    RuntimeException("Player already owns this quest. {playerId:$playerId, questId:$questId}")
