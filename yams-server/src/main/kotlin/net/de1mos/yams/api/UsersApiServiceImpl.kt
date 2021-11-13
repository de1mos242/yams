package net.de1mos.yams.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.de1mos.yams.api.model.User
import net.de1mos.yams.services.UsersManagementService
import org.springframework.stereotype.Service

@Service
class UsersApiServiceImpl(
    private val usersManagementService: UsersManagementService
) : UsersApiService {
    override fun usersGet(): Flow<User> {
        return usersManagementService.getUsers()
    }
}