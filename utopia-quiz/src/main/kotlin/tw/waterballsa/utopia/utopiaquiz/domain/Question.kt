package tw.waterballsa.utopia.utopiaquiz.domain

class Question(
    val id: Int,
    val description: String,
    val options: List<String>,
    private val correctChoice: Int
) {

    fun verify(answerChoice: Int): Boolean {
        return correctChoice == answerChoice
    }
}
