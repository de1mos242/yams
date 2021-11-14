package net.de1mos.yams.validators

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.CurrentUserDoesNotExistException
import net.de1mos.yams.services.UsersManagementService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UserValidatorTest {
    private lateinit var validator: UserValidator
    private lateinit var userManagementService: UsersManagementService

    @BeforeEach
    fun setUp() {
        userManagementService = mock()
        validator = UserValidator(userManagementService)
    }

    @Test
    fun `Success validation`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(true)
            validator.validate(42)
        }
    }

    @Test
    fun `User not found`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(false)
            assertThrows<CurrentUserDoesNotExistException> {
                validator.validate(42)
            }
        }
    }
}
