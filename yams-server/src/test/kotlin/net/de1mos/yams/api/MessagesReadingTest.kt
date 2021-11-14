package net.de1mos.yams.api

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.CurrentUserDoesNotExistException
import net.de1mos.yams.MessageContentTooLongException
import net.de1mos.yams.SearchUserNotProvided
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistException
import net.de1mos.yams.api.model.ErrorResponse
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.SearchType
import net.de1mos.yams.api.model.User
import net.de1mos.yams.services.MessagesManagementService
import net.de1mos.yams.validators.MessageRequestValidator
import net.de1mos.yams.validators.UserValidator
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@WebFluxTest(MessagesApiController::class)
@Import(MessagesApiServiceImpl::class)
class MessagesReadingTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var messagesManagementService: MessagesManagementService

    @MockBean
    private lateinit var messageRequestValidator: MessageRequestValidator

    @MockBean
    private lateinit var userValidator: UserValidator

    @Test
    fun `Successful message reading`() {
        val users = listOf(User(1, "a"), User(2, "b"), User(3, "c"))
        val messages = listOf(
            Message(1, "q1", users[0], users[1], Instant.now().atOffset(ZoneOffset.UTC)),
            Message(2, "q2", users[0], users[2], Instant.now().atOffset(ZoneOffset.UTC)),
            Message(3, "q3", users[0], users[1], Instant.now().atOffset(ZoneOffset.UTC)),
            Message(4, "q4", users[0], users[2], Instant.now().atOffset(ZoneOffset.UTC))
        )
        whenever(messagesManagementService.searchMessages(any(), any(), anyOrNull())).thenReturn(flowOf(*messages.toTypedArray()))

        webTestClient.get()
            .uri("/api/messages?searchType=sent")
            .header("X-CurrentUserId", "1")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Message::class.java)
            .contains(*messages.toTypedArray())
    }

    @Test
    fun `Current user does not exist`() {
        runBlocking {
            whenever(userValidator.validate(any())).thenThrow(CurrentUserDoesNotExistException())
        }

        val errorResponse = webTestClient.get()
            .uri("/api/messages?searchType=sent")
            .header("X-CurrentUserId", "1")
            .exchange()
            .expectStatus().isForbidden
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("Current user does not exist", errorResponse.message)
    }

    @Test
    fun `Search messages from user but without user`() {
        runBlocking {
            whenever(userValidator.validate(any())).thenThrow(SearchUserNotProvided())
        }

        val errorResponse = webTestClient.get()
            .uri("/api/messages?searchType=from")
            .header("X-CurrentUserId", "1")
            .exchange()
            .expectStatus().isBadRequest
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(errorResponse)
        assertEquals("User to search from not provided", errorResponse.message)
    }

}