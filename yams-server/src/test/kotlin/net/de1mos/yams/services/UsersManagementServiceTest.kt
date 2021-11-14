package net.de1mos.yams.services

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.DataMapper
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.records.UsersRecord
import net.de1mos.yams.repositories.UsersRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class UsersManagementServiceTest {

    private lateinit var usersManagementService: UsersManagementService
    private lateinit var usersRepository: UsersRepository

    @BeforeEach
    fun setUp() {
        usersRepository = mock()
        usersManagementService = UsersManagementService(usersRepository, DataMapper())
    }

    @Test
    fun getUsers() {
        val users = listOf(User(42, "den"), User(50, "vik"))
        whenever(usersRepository.getUsers()).thenReturn(flowOf(*users.map { UsersRecord(it.id, it.username) }.toTypedArray()))
        val actualUsers = usersManagementService.getUsers()
        runBlocking { assertEquals(users, actualUsers.toList()) }
    }

    @Test
    fun userExists() {
        runBlocking {
            whenever(usersRepository.userExists(42)).thenReturn(true)
            whenever(usersRepository.userExists(100500)).thenReturn(false)

            assertEquals(true, usersManagementService.userExists(42))
            assertEquals(false, usersManagementService.userExists(100500))
        }
    }
}