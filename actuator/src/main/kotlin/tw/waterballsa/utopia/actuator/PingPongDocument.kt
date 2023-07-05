package tw.waterballsa.utopia.actuator

import tw.waterballsa.utopia.mongo.gateway.Document
import tw.waterballsa.utopia.mongo.gateway.Id

@Document
data class PingPongDocument(
    @Id val userId: String,
    var created: Long
)
