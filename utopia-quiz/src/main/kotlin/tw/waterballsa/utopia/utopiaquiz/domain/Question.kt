package tw.waterballsa.utopia.utopiaquiz.domain

class Question(
    val description: String,
    val options: List<String>,
    val answerChoice: Int
) {
    fun verify(answer: Answer): Boolean {
        return answerChoice == answer.choice
    }
}
