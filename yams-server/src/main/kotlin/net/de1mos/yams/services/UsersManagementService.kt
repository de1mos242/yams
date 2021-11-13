package net.de1mos.yams.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.records.UsersRecord
import net.de1mos.yams.repositories.UsersRepository
import org.springframework.stereotype.Service

@Service
class UsersManagementService(private val usersRepository: UsersRepository) {

    fun getUsers(): Flow<User> {
        return usersRepository.getUsers().map { it.toApi() }
    }

    suspend fun registerUser(username: String): User {
        val record = UsersRecord()
        record.username = username
        return usersRepository.createUser(record).toApi()
    }

    private fun UsersRecord.toApi(): User {
        return User(this.id, this.username)
    }
}