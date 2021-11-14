package net.de1mos.yams.services

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.de1mos.yams.DataMapper
import net.de1mos.yams.SearchUserNotProvided
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.SearchType
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.Messages
import net.de1mos.yams.db.tables.Users
import net.de1mos.yams.db.tables.records.MessagesRecord
import net.de1mos.yams.db.tables.records.UsersRecord
import net.de1mos.yams.repositories.MessagesRepository
import org.jooq.Record
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

internal class MessagesManagementServiceTest {

    private lateinit var messagesManagementService: MessagesManagementService

    private lateinit var clock: Clock
    private lateinit var messagesRepository: MessagesRepository
    private lateinit var dataMapper: DataMapper

    @BeforeEach
    fun setUp() {
        clock = mock()
        messagesRepository = mock()
        dataMapper = DataMapper()
        messagesManagementService = MessagesManagementService(messagesRepository, dataMapper, clock)
    }

    @Test
    fun `Add new message`() {
        val now = Instant.now()
        val senderId = 42L
        val content = "myContent"
        val receiverId = 512L

        whenever(clock.instant()).thenReturn(now)
        val c = argumentCaptor<MessagesRecord>()
        runBlocking {
            messagesManagementService.addMessage(senderId, MessageRequest(content, receiverId))
            verify(messagesRepository).insert(c.capture())
        }

        val expected = MessagesRecord(null, senderId, receiverId, content, now.atOffset(ZoneOffset.UTC).toLocalDateTime())
        assertEquals(expected, c.firstValue)
    }

    @Test
    fun `Search from without senderId`() {
        assertThrows<SearchUserNotProvided> {
            messagesManagementService.searchMessages(42, SearchType.from, null)
        }
    }

    @Test
    fun `Search from`() {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val messages = listOf(
            Message(1, "q1", User(42, "den"), User(50, "vik"), now),
            Message(2, "q2", User(42, "den"), User(50, "vik"), now.plusMinutes(1)),
            Message(3, "q3", User(42, "den"), User(50, "vik"), now.plusMinutes(2)),
            Message(4, "q4", User(42, "den"), User(50, "vik"), now.plusMinutes(3))
        )
        whenever(messagesRepository.getMessagesFromUserToUser(50, 42)).thenReturn(flow {
            messages.forEach { m ->
                emit(buildRecord(m.id!!, m.sender!!.id!!, m.sender!!.username!!, m.receiver!!.id!!, m.receiver!!.username!!, m.content!!, m.timestamp!!.toLocalDateTime()))
            }
        })
        val flow = messagesManagementService.searchMessages(42, SearchType.from, 50)
        val actualMessages = runBlocking { flow.toList() }
        assertEquals(messages, actualMessages)
    }

    @Test
    fun `Search sent`() {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val messages = listOf(
            Message(1, "q1", User(42, "den"), User(50, "vik"), now),
            Message(2, "q2", User(42, "den"), User(25, "liza"), now.plusMinutes(1)),
            Message(3, "q3", User(42, "den"), User(30, "TT"), now.plusMinutes(2)),
            Message(4, "q4", User(42, "den"), User(50, "vik"), now.plusMinutes(3))
        )
        whenever(messagesRepository.getMessagesSentByUser(42)).thenReturn(flow {
            messages.forEach { m ->
                emit(buildRecord(m.id!!, m.sender!!.id!!, m.sender!!.username!!, m.receiver!!.id!!, m.receiver!!.username!!, m.content!!, m.timestamp!!.toLocalDateTime()))
            }
        })
        val flow = messagesManagementService.searchMessages(42, SearchType.sent, null)
        val actualMessages = runBlocking { flow.toList() }
        assertEquals(messages, actualMessages)
    }

    private fun buildRecord(id: Long, senderId: Long, senderName: String, receiverId: Long, receiverName: String, content: String, ts: LocalDateTime): Record {
        val record: Record = mock()
        whenever(record.into(Messages.MESSAGES.`as`("message"))).thenReturn(MessagesRecord(id, senderId, receiverId, content, ts))
        whenever(record.into(Users.USERS.`as`("sender"))).thenReturn(UsersRecord(senderId, senderName))
        whenever(record.into(Users.USERS.`as`("receiver"))).thenReturn(UsersRecord(receiverId, receiverName))
        return record
    }
}