package net.de1mos.yams.validators

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.MessageContentTooLongException
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistException
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.services.UsersManagementService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MessageRequestValidatorTest {

    private lateinit var validator: MessageRequestValidator
    private lateinit var userManagementService: UsersManagementService

    @BeforeEach
    fun setUp() {
        userManagementService = mock()
        validator = MessageRequestValidator(userManagementService)
    }

    @Test
    fun `Success validation`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(true)
            validator.validate(42, MessageRequest("hello", 100500))
        }
    }

    @Test
    fun `Receiver not exists`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(false)
            assertThrows<UserDoesNotExistException> {
                validator.validate(42, MessageRequest("hello", 100500))
            }
        }
    }

    @Test
    fun `Sender and receiver are the same`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(true)
            assertThrows<SenderAndReceiverAreSameException> {
                validator.validate(42, MessageRequest("hello", 42))
            }
        }
    }

    @Test
    fun `Content too long error`() {
        runBlocking {
            whenever(userManagementService.userExists(any())).thenReturn(true)
            assertThrows<MessageContentTooLongException> {
                validator.validate(42, MessageRequest("A".repeat(1001), 42))
            }
        }
    }
}