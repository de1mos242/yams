package net.de1mos.yams.validators

import net.de1mos.yams.CurrentUserDoesNotExistException
import net.de1mos.yams.services.UsersManagementService
import org.springframework.stereotype.Service

@Service
class UserValidator(
    private val usersManagementService: UsersManagementService
) {
    suspend fun validate(currentUserId: Long) {
        if (!usersManagementService.userExists(currentUserId)) {
            throw CurrentUserDoesNotExistException()
        }
    }
}