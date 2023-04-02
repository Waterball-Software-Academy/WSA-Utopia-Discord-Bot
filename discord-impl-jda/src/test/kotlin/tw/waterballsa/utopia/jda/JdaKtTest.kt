package tw.waterballsa.utopia.jda

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JdaKtTest {
    @Test
    fun `test should include listeners in the listeners package`() {
        val listeners = loadListenersFromAllUtopiaModules(null)
        assertThat(listeners).anyMatch { it.name == "testListener1" }
        assertThat(listeners).anyMatch { it.name == "testListener2" }
        assertThat(listeners).anyMatch { it.name == "testListener3" }
    }

}
