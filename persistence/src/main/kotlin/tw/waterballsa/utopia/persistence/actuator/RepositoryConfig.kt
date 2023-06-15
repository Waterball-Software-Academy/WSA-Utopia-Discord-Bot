package tw.waterballsa.utopia.mongo.actuator

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.actuator.PingPongRecord
import tw.waterballsa.utopia.actuator.PingPongRepository

@Component
class RepositoryConfig {

    @Bean
    fun pingPongRepository(pingPongMongoRepository: PingPongMongoRepository): PingPongRepository {
        return object : PingPongRepository {
            override fun save(entity: PingPongRecord): PingPongRecord {
                return pingPongMongoRepository.save(entity.toDocument()).toRecord()
            }

            override fun findOneByName(name: String): PingPongRecord? {
                return pingPongMongoRepository.findOneByName(name)?.toRecord()
            }

            override fun findAllByName(name: String): List<PingPongRecord> {
                return pingPongMongoRepository.findAllByName(name)
                        .map(PingPongDocument::toRecord)
            }
        }
    }
}
