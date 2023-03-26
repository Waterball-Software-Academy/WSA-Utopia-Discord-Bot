package tw.waterballsa.utopia.jda

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.utils.data.DataObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompositeListenerTest {
    private lateinit var compositeListener: CompositeListener

    @BeforeEach
    fun setUp() {
        compositeListener = CompositeListener()
    }

    @Test
    fun `register adds listener to the list`() {
        val listener = listener {}
        compositeListener.register(listener)
        assertThat(compositeListener.listeners).containsExactly(listener)
    }

    @Test
    fun `unregister removes listener from the list`() {
        val listener = listener {}
        compositeListener.register(listener)
        compositeListener.unregister(listener)
        assertThat(compositeListener.listeners).isEmpty()
    }

    @Test
    fun `onEvent dispatches event to all listeners`() {
        var count = 0
        val listener1 = listener { count += 1 }
        val listener2 = listener { count += 1 }
        compositeListener.register(listener1)
        compositeListener.register(listener2)
        compositeListener.onEvent(mockEvent())
        assertThat(count).isEqualTo(2)
    }

    private fun mockEvent(): GenericEvent {
        // create a mock event to pass to the listener
        return object : GenericEvent {
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
    }
}
