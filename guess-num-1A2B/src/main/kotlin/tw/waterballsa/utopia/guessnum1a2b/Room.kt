package tw.waterballsa.utopia.guessnum1a2b

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime


@Document(collection = "guess_num_1a2b_room")
data class Room(
    @Id
    val roomId: String,
    val playerId: String,
    val answer: String,
    @Field("guess_records")
    var guessRecords: MutableList<String> = mutableListOf(),
    @Field("is_victory")
    var isVictory: Boolean = false,
    @Field("create_date_time")
    val createdDateTime: LocalDateTime = LocalDateTime.now()
)
