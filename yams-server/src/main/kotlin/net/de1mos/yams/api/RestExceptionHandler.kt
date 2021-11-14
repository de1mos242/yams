package net.de1mos.yams.api

import net.de1mos.yams.CurrentUserDoesNotExistsException
import net.de1mos.yams.DuplicateUsernameException
import net.de1mos.yams.api.model.ErrorResponse
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
    fun serverExceptionHandler(ex: Exception): ErrorResponse {
        logger.error(ex.message, ex)
        return buildResponse(ex.message)
    }

    @ExceptionHandler(DuplicateUsernameException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(ex: DuplicateUsernameException): ErrorResponse {
        return buildResponse(ex.message)
    }

    @ExceptionHandler(CurrentUserDoesNotExistsException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handle(ex: CurrentUserDoesNotExistsException): ErrorResponse {
        return buildResponse(ex.message)
    }

    private fun buildResponse(message:String?): ErrorResponse {
        return ErrorResponse(message)
    }
}