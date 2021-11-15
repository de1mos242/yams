package net.de1mos.yams

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.Messages.MESSAGES
import net.de1mos.yams.db.tables.Users.USERS
import net.de1mos.yams.db.tables.records.MessagesRecord
import net.de1mos.yams.db.tables.records.UsersRecord
import org.jooq.Record
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

internal class DataMapperTest {

    private val dataMapper = DataMapper()

    @Test
    fun `convert user`() {
        val user = dataMapper.userToApi(UsersRecord(42, "den"))
        assertEquals(User(42, "den"), user)
    }

    @Test
    fun `convert message`() {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record: Record = mock()
        whenever(record.into(MESSAGES.`as`("message"))).thenReturn(MessagesRecord(1, 42, 50, "super", now.toLocalDateTime()))
        whenever(record.into(USERS.`as`("sender"))).thenReturn(UsersRecord(42, "den"))
        whenever(record.into(USERS.`as`("receiver"))).thenReturn(UsersRecord(50, "vik"))
        val message = dataMapper.messageToApi(record)
        assertEquals(Message(1, "super", User(42, "den"), User(50, "vik"), now), message)
    }


}