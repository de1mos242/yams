package net.de1mos.yams.api

import kotlinx.coroutines.runBlocking
import net.de1mos.yams.dto.MessageDto
import net.de1mos.yams.services.MessagesManagementService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class KafkaMessageListener(private val messagesManagementService: MessagesManagementService) {

    @KafkaListener(topics = ["\${yams.messages-topic}"])
    fun consume(message: MessageDto) = runBlocking {
        messagesManagementService.storeMessage(message)
    }
}