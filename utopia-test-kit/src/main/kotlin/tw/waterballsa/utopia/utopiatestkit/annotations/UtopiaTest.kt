package tw.waterballsa.utopia.utopiatestkit.annotations

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import tw.waterballsa.utopia.utopiatestkit.configs.MockUtopiaBeanConfig
import tw.waterballsa.utopia.utopiatestkit.configs.MongoDbTestContainerConfig
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Inherited
@Target(CLASS)
@Retention(RUNTIME)
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [MockUtopiaBeanConfig::class, MongoDbTestContainerConfig::class])
annotation class UtopiaTest