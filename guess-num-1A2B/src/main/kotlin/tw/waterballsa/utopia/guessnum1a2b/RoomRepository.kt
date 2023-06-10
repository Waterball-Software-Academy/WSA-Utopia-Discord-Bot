package tw.waterballsa.utopia.guessnum1a2b

import org.springframework.data.mongodb.repository.MongoRepository

interface RoomRepository : MongoRepository<Room, String>
