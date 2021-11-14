package net.de1mos.yams.api

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.CurrentUserDoesNotExistException
import net.de1mos.yams.MessageContentTooLongException
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistException
import net.de1mos.yams.api.model.ErrorResponse
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.services.MessagesManagementService
import net.de1mos.yams.validators.MessageRequestValidator
import net.de1mos.yams.validators.UserValidator
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@WebFluxTest(MessagesApiController::class)
@Import(MessagesApiServiceImpl::class)
class MessagesSendingTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var messagesManagementService: MessagesManagementService

    @MockBean
    private lateinit var messageRequestValidator: MessageRequestValidator

    @MockBean
    private lateinit var userValidator: UserValidator

    @Test
    fun `Successful message sending`() {
        val senderId = 42
        val receiverId = 100500L
        val content = "Hello, my dear friend"
        runBlocking {
            whenever(messagesManagementService.addMessage(any(), any())).thenReturn(null)
        }

        webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest(content, receiverId)))
            .header("X-CurrentUserId", senderId.toString())
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `Don't send current user id`() {
        val receiverId = 100500L
        val content = "Hello, my dear friend"
        runBlocking {
            whenever(messagesManagementService.addMessage(any(), any())).thenReturn(null)
        }

        val errorResponse = webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest(content, receiverId)))
            .exchange()
            .expectStatus().isBadRequest
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("Missing request header 'X-CurrentUserId' for method parameter of type long", errorResponse.message)
    }

    @Test
    fun `Current user does not exist`() {
        runBlocking {
            whenever(userValidator.validate(any())).thenThrow(CurrentUserDoesNotExistException())
        }

        val errorResponse = webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest("hey", 100500)))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isForbidden
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("Current user does not exist", errorResponse.message)
    }

    @Test
    fun `Receiver does not exist`() {
        runBlocking {
            whenever(messageRequestValidator.validate(any(), any())).thenThrow(UserDoesNotExistException(100500))
        }

        val errorResponse = webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest("hey", 100500)))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isNotFound
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("User 100500 does not exist", errorResponse.message)
    }

    @Test
    fun `Sender and receiver are the same`() {
        runBlocking {
            whenever(messageRequestValidator.validate(any(), any())).thenThrow(SenderAndReceiverAreSameException())
        }

        val errorResponse = webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest("hey", 100500)))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isBadRequest
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("Can't send message to self", errorResponse.message)
    }

    @Test
    fun `Send too long message`() {
        runBlocking {
            whenever(messageRequestValidator.validate(any(), any())).thenThrow(MessageContentTooLongException())
        }

        val errorResponse = webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest("any", 55)))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isBadRequest
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("Message content is too long", errorResponse.message)
    }

    @Test
    fun `Send message with missing content`() {
        runBlocking {
            whenever(messagesManagementService.addMessage(any(), any())).thenReturn(null)
        }

        val requestBody = JSONObject()
        requestBody.put("receiverId", 50)
        webTestClient.post()
            .uri("/api/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody.toString()))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `Send message with missing receiverId`() {
        runBlocking {
            whenever(messagesManagementService.addMessage(any(), any())).thenReturn(null)
        }

        val requestBody = JSONObject()
        requestBody.put("content", "wrong")
        webTestClient.post()
            .uri("/api/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody.toString()))
            .header("X-CurrentUserId", "42")
            .exchange()
            .expectStatus().isBadRequest
    }
}