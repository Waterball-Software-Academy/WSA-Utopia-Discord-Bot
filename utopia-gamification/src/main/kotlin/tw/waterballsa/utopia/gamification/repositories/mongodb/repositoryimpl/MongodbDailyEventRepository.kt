package tw.waterballsa.utopia.gamification.repositories.mongodb.repositoryimpl

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.gamification.dailyrank.domain.DailyEvent
import tw.waterballsa.utopia.gamification.repositories.DailyEventRepository
import tw.waterballsa.utopia.mongo.gateway.Id
import tw.waterballsa.utopia.mongo.gateway.MongoCollection

@Component
class MongodbDailyEventRepository(
    private val dailyEventRepository: MongoCollection<DailyEventDocument, String>
) : DailyEventRepository{

    override fun save(event: DailyEvent): DailyEvent {
        dailyEventRepository.save(event.toDocument())
        return event
    }

    override fun findNotPostedEvent(): List<DailyEvent> =
        dailyEventRepository.findAll()
            .map { it.toDomain() }



    private fun DailyEvent.toDocument() =
        DailyEventDocument(playerId)

}


// TODO document data model
@Document
data class DailyEventDocument(
    @Id val id: String

){

    fun toDomain() : DailyEvent = DailyEvent(id)
}
