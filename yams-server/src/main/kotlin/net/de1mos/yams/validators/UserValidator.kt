package net.de1mos.yams.validators

import net.de1mos.yams.CurrentUserDoesNotExistsException
import net.de1mos.yams.SenderAndReceiverAreSameException
import net.de1mos.yams.UserDoesNotExistsException
import net.de1mos.yams.api.model.MessageRequest
import net.de1mos.yams.services.UsersManagementService
import org.springframework.stereotype.Service

@Service
class UserValidator(
    private val usersManagementService: UsersManagementService
) {
    suspend fun validate(currentUserId: Long) {
        if (!usersManagementService.userExists(currentUserId)) {
            throw CurrentUserDoesNotExistsException()
        }
    }
}