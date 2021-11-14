package net.de1mos.yams.it

import com.nhaarman.mockitokotlin2.whenever
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.User
import net.de1mos.yams.api.model.UserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WholeCycleTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `register 3 users, send messages and get history`() {
        val username1 = UUID.randomUUID().toString()
        val userId1 = registerUser(username1)

        val username2 = UUID.randomUUID().toString()
        val userId2 = registerUser(username2)

        val username3 = UUID.randomUUID().toString()
        val userId3 = registerUser(username3)

        sendMessage(userId1, userId2, "m1")
        sendMessage(userId2, userId3, "m2")
        sendMessage(userId3, userId1, "m3")
        sendMessage(userId1, userId3, "m4")
        sendMessage(userId1, userId2, "m5")
        sendMessage(userId2, userId1, "m6")

        val user1 = User(userId1, username1)
        val user2 = User(userId2, username2)
        val user3 = User(userId3, username3)
        val messages = listOf(
            Message(1, "m1", user1, user2, OffsetDateTime.now()),
            Message(2, "m2", user2, user3, OffsetDateTime.now()),
            Message(3, "m3", user3, user1, OffsetDateTime.now()),
            Message(4, "m4", user1, user3, OffsetDateTime.now()),
            Message(5, "m5", user1, user2, OffsetDateTime.now()),
            Message(6, "m6", user2, user1, OffsetDateTime.now())
        )

        val sentMessages = getSentMessages(userId1)
        val expectedSentMessages = messages.filter { it.sender!!.id == userId1 }.sortedBy { it.id }
        assertThat(sentMessages).usingElementComparatorIgnoringFields("id", "timestamp").isEqualTo(expectedSentMessages)

        val receivedMessages = getReceivedMessages(userId1)
        val expectedReceivedMessages = messages.filter { it.receiver!!.id == userId1 }.sortedBy { it.id }
        assertThat(receivedMessages).usingElementComparatorIgnoringFields("id", "timestamp").isEqualTo(expectedReceivedMessages)

        val fromMessages = getFromMessages(userId1, userId2)
        val expectedFromMessages = messages.filter { it.sender!!.id == userId1 }.filter { it.receiver!!.id == userId2 }.sortedBy { it.id }
        assertThat(fromMessages).usingElementComparatorIgnoringFields("id", "timestamp").isEqualTo(expectedFromMessages)
    }

    private fun registerUser(username: String) = webTestClient.post()
        .uri("/api/users")
        .body(BodyInserters.fromValue(UserRequest(username)))
        .exchange()
        .expectStatus().isOk
        .returnResult(User::class.java)
        .responseBody
        .blockFirst()!!
        .id!!

    private fun sendMessage(from: Long, to: Long, content: String) {
        webTestClient.post()
            .uri("/api/messages")
            .body(BodyInserters.fromValue(MessageRequest(content, to)))
            .header("X-CurrentUserId", "$from")
            .exchange()
            .expectStatus().isOk
    }

    private fun getSentMessages(userId: Long): List<Message> {
        return webTestClient.get()
            .uri("/api/messages?searchType=sent")
            .header("X-CurrentUserId", "$userId")
            .exchange()
            .expectStatus().isOk
            .returnResult(Message::class.java)
            .responseBody
            .collectList()
            .block()!!
    }

    private fun getReceivedMessages(userId: Long): List<Message> {
        return webTestClient.get()
            .uri("/api/messages?searchType=received")
            .header("X-CurrentUserId", "$userId")
            .exchange()
            .expectStatus().isOk
            .returnResult(Message::class.java)
            .responseBody
            .collectList()
            .block()!!
    }

    private fun getFromMessages(fromId: Long, toId: Long): List<Message> {
        return webTestClient.get()
            .uri("/api/messages?searchType=from&userId=$fromId")
            .header("X-CurrentUserId", "$toId")
            .exchange()
            .expectStatus().isOk
            .returnResult(Message::class.java)
            .responseBody
            .collectList()
            .block()!!
    }
}