package tw.waterballsa.utopia.jda.domains

interface EventPublisher {
    fun broadcastEvent(utopiaEvent: UtopiaEvent) {}
}

interface UtopiaEvent