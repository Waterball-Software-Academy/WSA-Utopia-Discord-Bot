package tw.waterballsa.utopia.utopiatestkit.components

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.jda.domains.EventPublisher

@Component
class TestComponent(private val eventPublisher: EventPublisher)