package tw.waterballsa.utopia.utopiatestkit.configs

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.mockito.Mockito.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.domains.EventPublisher
import java.util.*
import java.util.UUID.randomUUID

@ComponentScan(basePackages = ["tw.waterballsa.utopia"])
open class MockUtopiaBeanConfig {

    @Bean
    open fun jda(): JDA = mock(JDA::class.java)

    @Bean
    open fun guild(): Guild = mock(Guild::class.java)

    @Bean
    open fun eventPublisher(): EventPublisher = mock(EventPublisher::class.java)

    @Bean
    open fun wsaDiscordProperties(): WsaDiscordProperties = WsaDiscordProperties(mockProperties())

    @Bean
    open fun mockProperties(): Properties = mock(Properties::class.java)
            .apply { `when`(getProperty(anyString())).thenReturn(randomUUID().toString()) }

}