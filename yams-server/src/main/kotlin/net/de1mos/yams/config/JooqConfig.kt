package net.de1mos.yams.config

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JooqConfig(private val cfi: ConnectionFactory) {
    @Bean
    fun jooqDslContext(): DSLContext {
        return DSL.using(cfi).dsl()
    }

}