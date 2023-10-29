package tw.waterballsa.utopia.utopiatestkit.annotations

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import tw.waterballsa.utopia.utopiatestkit.configs.MockUtopiaBeanConfig
import tw.waterballsa.utopia.utopiatestkit.configs.MongoDbTestContainerExtension
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Inherited
@Target(CLASS)
@Retention(RUNTIME)
@ContextConfiguration(classes = [MockUtopiaBeanConfig::class])
@ExtendWith(value = [SpringExtension::class, MongoDbTestContainerExtension::class])
annotation class UtopiaTest