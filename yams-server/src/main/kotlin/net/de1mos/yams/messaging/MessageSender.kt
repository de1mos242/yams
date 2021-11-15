package net.de1mos.yams.messaging

import kotlinx.coroutines.suspendCancellableCoroutine
import net.de1mos.yams.dto.MessageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Service
class MessageSender(
    private val kafkaTemplate: KafkaTemplate<String, MessageDto>,
    @Value("\${yams.messages-topic}") private val messageTopic: String
) {

    suspend fun sendMessage(messageDto: MessageDto) {
        val future = kafkaTemplate.send(messageTopic, messageDto.senderId.toString(), messageDto)
        suspendCancellableCoroutine<SendResult<String, MessageDto>> { cont ->
            future.addCallback(
                { cont.resume(it!!) },
                { cont.resumeWithException(it) })
        }
    }
}