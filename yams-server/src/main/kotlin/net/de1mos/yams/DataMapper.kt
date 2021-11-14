package net.de1mos.yams

import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.User
import net.de1mos.yams.db.tables.Messages
import net.de1mos.yams.db.tables.Users
import net.de1mos.yams.db.tables.records.UsersRecord
import org.jooq.Record
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class DataMapper {
    fun userToApi(record: UsersRecord): User {
        return User(record.id, record.username)
    }

    fun messageToApi(record: Record): Message {
        val m = record.into(Messages.MESSAGES.`as`("message"))
        val sender = record.into(Users.USERS.`as`("sender"))
        val receiver = record.into(Users.USERS.`as`("receiver"))
        return Message(m.id, m.content, userToApi(sender), userToApi(receiver), m.messageTimestamp)
    }
}