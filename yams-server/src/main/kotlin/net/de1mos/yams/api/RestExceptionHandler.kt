package net.de1mos.yams.api

import net.de1mos.yams.BadRequestException
import net.de1mos.yams.ConflictException
import net.de1mos.yams.CurrentUserDoesNotExistException
import net.de1mos.yams.UserDoesNotExistException
import net.de1mos.yams.api.model.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebInputException


@RestControllerAdvice
class RestExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun serverExceptionHandler(ex: Exception): ErrorResponse {
        logger.error(ex.message, ex)
        return buildResponse(ex.message)
    }

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun serverExceptionHandler(ex: ServerWebInputException): ErrorResponse {
        logger.error(ex.message, ex)
        return buildResponse(ex.reason)
    }

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handle(ex: ConflictException): ErrorResponse {
        return buildResponse(ex.message)
    }

    @ExceptionHandler(CurrentUserDoesNotExistException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handle(ex: CurrentUserDoesNotExistException): ErrorResponse {
        return buildResponse(ex.message)
    }

    @ExceptionHandler(UserDoesNotExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(ex: UserDoesNotExistException): ErrorResponse {
        return buildResponse(ex.message)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(ex: BadRequestException): ErrorResponse {
        return buildResponse(ex.message)
    }

    private fun buildResponse(message: String?): ErrorResponse {
        return ErrorResponse(message)
    }
}