package tw.waterballsa.utopia.utopiagamificationquest.domain.buttons


const val BUTTON_QUEST_TAG = "quest"

class RewardButton {
    companion object {
        const val NAME = "rewardButton"
        const val LABEL: String = "領取獎勵"

        fun id(questTitle: String): String = "$BUTTON_QUEST_TAG-$NAME-$questTitle"
    }
}
