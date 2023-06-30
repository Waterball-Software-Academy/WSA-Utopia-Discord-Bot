package tw.waterballsa.utopia.utopiagamificationquest.domain.buttons

class QuizButton {
    companion object {
        const val NAME = "quizButton"
        const val LABEL: String = "開始考試"

        fun id(): String = "$BUTTON_QUEST_TAG-$NAME"
    }
}
