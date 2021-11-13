package net.de1mos.yams.repositories

import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class MessagesRepository(private val ctx: DSLContext) {

}