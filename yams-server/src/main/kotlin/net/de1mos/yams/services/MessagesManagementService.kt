package net.de1mos.yams.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.de1mos.yams.DataMapper
import net.de1mos.yams.SearchUserNotProvided
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.SearchType
import net.de1mos.yams.db.tables.records.MessagesRecord
import net.de1mos.yams.repositories.MessagesRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class MessagesManagementService(
    private val messagesRepository: MessagesRepository,
    private val dataMapper: DataMapper,
    private val clock: Clock
) {

    suspend fun addMessage(senderId: Long, messageRequest: MessageRequest) {
        val ts = clock.instant().atOffset(ZoneOffset.UTC)
        val message = MessagesRecord()
        message.content = messageRequest.content
        message.senderId = senderId
        message.receiverId = messageRequest.receiverId
        message.messageTimestamp = ts.toLocalDateTime()
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