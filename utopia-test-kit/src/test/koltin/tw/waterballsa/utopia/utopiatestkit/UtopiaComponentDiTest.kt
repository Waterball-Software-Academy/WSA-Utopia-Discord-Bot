package tw.waterballsa.utopia.utopiatestkit

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import tw.waterballsa.utopia.utopiatestkit.annotations.UtopiaTest
import tw.waterballsa.utopia.utopiatestkit.components.TestComponent

@UtopiaTest
class UtopiaComponentDiTest @Autowired constructor(private val factory: BeanFactory) {

    @Test
    fun testDI() {
        assertNotNull(factory.getBean(MongoTemplate::class.java))
        assertNotNull(factory.getBean(TestComponent::class.java))
    }
}