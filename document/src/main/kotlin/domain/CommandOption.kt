package domain

data class CommandOption(
        val name: String,
        val type: String,
        val description: String
) {
    fun toDocument(): String {
        return "$name($type): $description"
    }
}
