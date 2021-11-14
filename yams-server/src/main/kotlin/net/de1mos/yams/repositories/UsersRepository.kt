package net.de1mos.yams.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import net.de1mos.yams.DuplicateUsernameException
import net.de1mos.yams.db.tables.Users.USERS
import net.de1mos.yams.db.tables.records.UsersRecord
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.jooq.exception.SQLStateClass
import org.jooq.impl.DSL.exists
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class UsersRepository(private val dslContext: DSLContext) {
    fun getUsers(): Flow<UsersRecord> {
        return dslContext.selectFrom(USERS).asFlow()
    }

    suspend fun insert(record: UsersRecord): UsersRecord {
        val awaitSingle = try {
            dslContext.insertInto(USERS).set(record).returning().awaitSingle()
        } catch (e: DataAccessException) {
            if (e.sqlStateClass() == SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION) {
                throw DuplicateUsernameException(record.username)
            } else {
                throw e
            }
        }
        return awaitSingle
    }

    suspend fun userExists(userId: Long): Boolean {
        val mono = Mono.from(dslContext.selectFrom(USERS).where(USERS.ID.eq(userId)))
        val record = mono.awaitFirstOrNull()
        return record != null
    }
}