package tw.waterballsa.utopia.jda

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.utils.data.DataObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.jda.domains.UtopiaEvent

class UtopiaListenerChainTest {

    private lateinit var utopiaListenerChain: UtopiaListenerChain

    @BeforeEach
    fun setUp() {
        utopiaListenerChain = UtopiaListenerChain()
    }

    @Test
    fun `register adds listener to the list`() {
        val listener = listener {}
        utopiaListenerChain.register(listener)
        assertThat(utopiaListenerChain.deprecatedListeners).containsExactly(listener)
    }

    @Test
    fun `onEvent dispatches event to all listeners`() {
        var count = 0
        val listener1 = listener { count += 1 }
        val listener2 = listener { count += 1 }
        utopiaListenerChain.register(listener1)
        utopiaListenerChain.register(listener2)
        utopiaListenerChain.onEvent(MockEvent())
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `onUtopiaEvent dispatches event to listener`() {
        val listener = MockUtopiaListener(utopiaListenerChain)
        utopiaListenerChain.register(listener)
        utopiaListenerChain.onEvent(MockEvent())
        assertThat(listener.hasMockUtopiaEvent).isTrue
    }

}

// create a mock event to pass to the listener
class MockEvent : GenericEvent {
    override fun getJDA(): JDA {
        throw UnsupportedOperationException()
    }

    override fun getResponseNumber(): Long {
        throw UnsupportedOperationException()
    }

    override fun getRawData(): DataObject? {
        throw UnsupportedOperationException()
    }
}

class MockUtopiaListener(private val eventPublisher: EventPublisher) : UtopiaListener() {

    var hasMockUtopiaEvent = false
        private set

    override fun onGenericEvent(event: GenericEvent) {
        eventPublisher.broadcastEvent(MockUtopiaEvent())
    }

    override fun onUtopiaEvent(event: UtopiaEvent) {
        hasMockUtopiaEvent = event is MockUtopiaEvent
    }
}

class MockUtopiaEvent : UtopiaEvent