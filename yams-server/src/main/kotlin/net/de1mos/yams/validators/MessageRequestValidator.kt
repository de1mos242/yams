package net.de1mos.yams.validators

import net.de1mos.yams.CurrentUserDoesNotExistsException
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistsException
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.services.UsersManagementService
import org.springframework.stereotype.Service

@Service
class MessageRequestValidator(
    private val usersManagementService: UsersManagementService
) {
    suspend fun validate(currentUserId: Long, messageRequest: MessageRequest) {
        if (!usersManagementService.userExists(messageRequest.receiverId)) {
            throw UserDoesNotExistsException(messageRequest.receiverId)
        }
        if (currentUserId == messageRequest.receiverId) {
            throw SenderAndReceiverAreSameException()
        }
    }
}