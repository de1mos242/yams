package net.de1mos.yams.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MessageDto(
    @field:JsonProperty("senderId", required = true) val senderId: Long,
    @field:JsonProperty("receiverId", required = true) val receiverId: Long,
    @field:JsonProperty("content", required = true) val content: String,
    @field:JsonProperty("ts", required = true) val ts: LocalDateTime
)