package net.de1mos.yams.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.api.model.SearchType
import net.de1mos.yams.api.model.User
import net.de1mos.yams.services.MessagesManagementService
import net.de1mos.yams.validators.MessageRequestValidator
import net.de1mos.yams.validators.UserValidator
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.ZoneOffset

@Service
class MessagesApiServiceImpl(
    private val messagesManagementService: MessagesManagementService,
    private val messageRequestValidator: MessageRequestValidator,
    private val userValidator: UserValidator
) : MessagesApiService {
    override fun searchMessages(xCurrentUserId: Long, searchType: SearchType, userId: Long?): Flow<Message> {
        return flow {
            userValidator.validate(xCurrentUserId)
            emit(null)
        }.flatMapConcat {
            messagesManagementService.searchMessages(xCurrentUserId, searchType, userId)
        }
    }

    override suspend fun sendMessage(xCurrentUserId: Long, messageRequest: MessageRequest) {
        userValidator.validate(xCurrentUserId)
        messageRequestValidator.validate(xCurrentUserId, messageRequest)
        messagesManagementService.addMessage(xCurrentUserId, messageRequest)
    }

}