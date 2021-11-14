package net.de1mos.yams.api

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.DuplicateUsernameException
import net.de1mos.yams.api.model.ErrorResponse
import net.de1mos.yams.api.model.User
import net.de1mos.yams.api.model.UserRequest
import net.de1mos.yams.services.UsersManagementService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@WebFluxTest(UsersApiController::class)
@Import(UsersApiServiceImpl::class)
class UsersApiControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var usersManagementService: UsersManagementService

    @Test
    fun `Successful user creation`() {
        val username = "My dear user"
        runBlocking {
            whenever(usersManagementService.registerUser(username)).thenReturn(User(42, username))
        }

        val user = webTestClient.post()
            .uri("/api/users")
            .body(BodyInserters.fromValue(UserRequest(username)))
            .exchange()
            .expectStatus().isOk
            .returnResult(User::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(user)
        assertEquals(42, user.id)
        assertEquals(username, user.username)
    }

    @Test
    fun `Register user with duplicate`() {
        val username = UUID.randomUUID().toString()

        runBlocking {
            whenever(usersManagementService.registerUser(username)).thenThrow(DuplicateUsernameException(username))
        }

        val error = webTestClient.post()
            .uri("/api/users")
            .body(BodyInserters.fromValue(UserRequest(username)))
            .exchange()
            .expectStatus().is4xxClientError
            .returnResult(ErrorResponse::class.java)
            .responseBody
            .blockFirst()
        assertNotNull(error)
        assertNotNull(error.message)
        assertEquals("Username $username already exists", error.message)
    }
}