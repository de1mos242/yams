package net.de1mos.yams.api

import kotlinx.coroutines.flow.Flow
import net.de1mos.yams.api.model.Message
import net.de1mos.yams.api.model.MessageRequest
import org.springframework.stereotype.Service

@Service
class MessagesApiServiceImpl : MessagesApiService {
    override fun searchMessages(xCurrentUserId: String, searchType: String, userId: Long?): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(xCurrentUserId: String, messageRequest: MessageRequest) {
        TODO("Not yet implemented")
    }

}