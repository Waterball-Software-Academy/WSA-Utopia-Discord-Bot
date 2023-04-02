package tw.waterballsa.utopia.jda

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class JdaKtTest {
    @Test
    fun `test should include listeners in the listeners package`() {
        val context = AnnotationConfigApplicationContext()
        val listeners = loadListenersFromAllUtopiaModules(context)
        assertThat(listeners).anyMatch { it.name == "testListener1" }
        assertThat(listeners).anyMatch { it.name == "testListener2" }
        assertThat(listeners).anyMatch { it.name == "testListener3" }
    }

}
