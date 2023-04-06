package tw.waterballsa.utopia.guessnum1a2b

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

class GameRepository {
    var gameRooms = mutableListOf<GameRoom>()

    fun register(member: Member, channel: ThreadChannel) {
        gameRooms.add(GameRoom(member, channel))
        logger.info {
            "[GameRepository register event] " + "{" + "\"feature\" : \"guess-num-1a2b\"," + "\"game room count\" : \"${gameRooms.size}\"," + "}"
        }
    }

    fun unregister(member: Member, channel: ThreadChannel) {

        val unregisteredGameRoom = gameRooms.filter { room ->
            room.member == member && room.threadChannel == channel
        }.onEach { it.close() }
        gameRooms -= unregisteredGameRoom

        logger.info { "[GameRoom Unregistered]  { \"gameRoomCount\" : \"${gameRooms.size}\"}" }
    }

    fun find(member: Member, channel: ThreadChannel): GameRoom? {
        return gameRooms.firstOrNull { room ->
            room.member == member && room.threadChannel == channel
        }
    }

    fun find(member: Member): GameRoom? {
        return gameRooms.firstOrNull { room ->
            room.member == member
        }
    }

}
