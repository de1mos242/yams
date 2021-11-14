package net.de1mos.yams.validators

import net.de1mos.yams.MessageContentTooLongException
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistException
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.services.UsersManagementService
import org.springframework.stereotype.Service

@Service
class MessageRequestValidator(
    private val usersManagementService: UsersManagementService
) {
    suspend fun validate(currentUserId: Long, messageRequest: MessageRequest) {
        if (messageRequest.content.length > 1000) {
            throw MessageContentTooLongException()
        }
        if (!usersManagementService.userExists(messageRequest.receiverId)) {
            throw UserDoesNotExistException(messageRequest.receiverId)
        }
        if (currentUserId == messageRequest.receiverId) {
            throw SenderAndReceiverAreSameException()
        }
    }
}