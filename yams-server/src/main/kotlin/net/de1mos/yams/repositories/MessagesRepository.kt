package net.de1mos.yams.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.de1mos.yams.db.tables.Messages.MESSAGES
import net.de1mos.yams.db.tables.Users.USERS
import net.de1mos.yams.db.tables.records.MessagesRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class MessagesRepository(private val ctx: DSLContext) {
    suspend fun insert(message: MessagesRecord): Long {
        return ctx.insertInto(MESSAGES).set(message).returning(MESSAGES.ID).awaitSingle().id
    }

    fun getMessagesFromUserToUser(senderId: Long, receiverId: Long): Flow<Record> {
        val sender = USERS.`as`("sender")
        val receiver = USERS.`as`("receiver")
        val messages = MESSAGES.`as`("message")
        return Flux.from(ctx.select().from(messages)
            .innerJoin(sender).on(messages.SENDER_ID.eq(sender.ID))
            .innerJoin(receiver).on(messages.RECEIVER_ID.eq(receiver.ID))
            .where(messages.SENDER_ID.eq(senderId))
            .and(messages.RECEIVER_ID.eq(receiverId)))
            .asFlow()
    }

    fun getMessagesSentByUser(senderId: Long): Flow<Record> {
        val sender = USERS.`as`("sender")
        val receiver = USERS.`as`("receiver")
        val messages = MESSAGES.`as`("message")
        return Flux.from(ctx.select().from(messages)
            .innerJoin(sender).on(messages.SENDER_ID.eq(sender.ID))
            .innerJoin(receiver).on(messages.RECEIVER_ID.eq(receiver.ID))
            .where(messages.SENDER_ID.eq(senderId)))
            .asFlow()
    }

    fun getMessagesReceivedByUser(receiverId: Long): Flow<Record> {
        val sender = USERS.`as`("sender")
        val receiver = USERS.`as`("receiver")
        val messages = MESSAGES.`as`("message")
        return Flux.from(ctx.select().from(messages)
            .innerJoin(sender).on(messages.SENDER_ID.eq(sender.ID))
            .innerJoin(receiver).on(messages.RECEIVER_ID.eq(receiver.ID))
            .where(messages.RECEIVER_ID.eq(receiverId)))
            .asFlow()
    }
}