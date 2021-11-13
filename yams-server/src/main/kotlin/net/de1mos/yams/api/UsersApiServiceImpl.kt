package net.de1mos.yams.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.de1mos.yams.api.model.User
import org.springframework.stereotype.Service

@Service
class UsersApiServiceImpl : UsersApiService {
    override fun usersGet(): Flow<User> {
        return flow {
            repeat(10) {
                emit(User("user-$it"))
            }
        }
    }
}