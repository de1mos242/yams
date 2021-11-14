package net.de1mos.yams

import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.Messages
import net.de1mos.yams.db.tables.Users
import net.de1mos.yams.db.tables.records.UsersRecord
import org.jooq.Record
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

@Service
class DataMapper {
    fun userToApi(record: UsersRecord): User {
        return User(record.id, record.username)
    }

    fun messageToApi(record: Record): Message {
        val m = record.into(Messages.MESSAGES.`as`("message"))
        val sender = record.into(Users.USERS.`as`("sender"))
        val receiver = record.into(Users.USERS.`as`("receiver"))
        val timestamp = m.messageTimestamp.atOffset(ZoneOffset.UTC)
        return Message(m.id, m.content, userToApi(sender), userToApi(receiver), timestamp)
    }
}