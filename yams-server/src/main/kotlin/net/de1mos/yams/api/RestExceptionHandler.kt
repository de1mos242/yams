package net.de1mos.yams.api

import net.de1mos.yams.DuplicateUsernameException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class RestExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun serverExceptionHandler(ex: Exception): String? {
        logger.error(ex.message, ex)
        return ex.message
    }

    @ExceptionHandler(DuplicateUsernameException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(ex: DuplicateUsernameException): String? {
        return ex.message
    }
}