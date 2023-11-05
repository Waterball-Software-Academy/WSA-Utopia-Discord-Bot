package tw.waterballsa.utopia.utopiagamification.quest.domain.exception

class ClaimInProgressMissionException(questTitle: String) : RuntimeException("任務未完成 $questTitle，不能領取獎勵")
