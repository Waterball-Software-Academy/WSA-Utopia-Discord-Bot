package tw.waterballsa.alpha.wsabot.gamification.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import me.jakejmattson.discordkt.annotations.Service
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author - johnny850807@gmail.com (Waterball)
 */
@Service
class StatisticsService {
    private val map = mutableMapOf<Snowflake, AtomicInteger>()

    fun incrementReaction(user: User): Int {
        return map.getOrPut(user.id, { AtomicInteger(0) })
            .incrementAndGet()
    }

    fun decrementReaction() {

    }
}