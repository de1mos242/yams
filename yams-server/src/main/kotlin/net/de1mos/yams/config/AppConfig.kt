package net.de1mos.yams.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class AppConfig {

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}