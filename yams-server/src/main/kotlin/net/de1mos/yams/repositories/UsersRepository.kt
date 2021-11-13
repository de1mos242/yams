package net.de1mos.yams.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.de1mos.yams.db.tables.Users
import net.de1mos.yams.db.tables.records.UsersRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class UsersRepository(private val dslContext: DSLContext) {
    fun getUsers(): Flow<UsersRecord> {
        return Flux.from(dslContext.selectFrom(Users.USERS)).asFlow()
    }
}