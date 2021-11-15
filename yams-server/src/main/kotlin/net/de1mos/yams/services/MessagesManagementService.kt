package net.de1mos.yams.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.de1mos.yams.DataMapper
import net.de1mos.yams.SearchUserNotProvided
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.SearchType
import net.de1mos.yams.db.tables.records.MessagesRecord
import net.de1mos.yams.dto.MessageDto
import net.de1mos.yams.messaging.MessageSender
import net.de1mos.yams.repositories.MessagesRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.ZoneOffset

@Service
class MessagesManagementService(
    private val messagesRepository: MessagesRepository,
    private val dataMapper: DataMapper,
    private val clock: Clock,
    private val messageSender: MessageSender
) {

    suspend fun addMessage(senderId: Long, messageRequest: MessageRequest) {
        val ts = clock.instant().atOffset(ZoneOffset.UTC).toLocalDateTime()
        messageSender.sendMessage(MessageDto(senderId, messageRequest.receiverId, messageRequest.content, ts))
    }

    suspend fun storeMessage(messageDto: MessageDto) {
        val message = MessagesRecord()
        message.content = messageDto.content
        message.senderId = messageDto.senderId
        message.receiverId = messageDto.receiverId
        message.messageTimestamp = messageDto.ts
        messagesRepository.insert(message)
    }

    fun searchMessages(currentUserId: Long, searchType: SearchType, userId: Long?): Flow<Message> {
        val flow = when (searchType) {
            SearchType.from -> {
                val fromUser = userId ?: throw SearchUserNotProvided()
                messagesRepository.getMessagesFromUserToUser(fromUser, currentUserId)
            }
            SearchType.sent -> messagesRepository.getMessagesSentByUser(currentUserId)
            SearchType.received -> messagesRepository.getMessagesReceivedByUser(currentUserId)
        }
        return flow.map {
            dataMapper.messageToApi(it)
        }
    }
}