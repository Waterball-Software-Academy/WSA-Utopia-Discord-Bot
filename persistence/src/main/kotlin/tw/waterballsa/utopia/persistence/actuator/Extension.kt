package tw.waterballsa.utopia.mongo.actuator

import tw.waterballsa.utopia.actuator.PingPongRecord
import java.time.Instant

fun PingPongRecord.toDocument(): PingPongDocument {
    return PingPongDocument(name, memo, Instant.now())
}

fun PingPongDocument.toRecord(): PingPongRecord {
    return PingPongRecord(name, memo)
}
