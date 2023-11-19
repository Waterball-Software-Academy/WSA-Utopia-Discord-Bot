package tw.waterballsa.utopia.utopiagamification.quest.domain.exception

class ClaimedMissionException(questTitle: String) : RuntimeException("已經領取過 $questTitle 的任務獎勵了，不能再領了")
