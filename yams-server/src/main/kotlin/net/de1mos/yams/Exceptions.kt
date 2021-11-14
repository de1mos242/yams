package net.de1mos.yams

open class BadRequestException(message: String): RuntimeException(message)

open class ConflictException(message: String): RuntimeException(message)

class DuplicateUsernameException(username: String) : ConflictException("Username $username already exists")

class SenderAndReceiverAreSameException : BadRequestException("Can't send message to self")

class SearchUserNotProvided : BadRequestException("User to search from not provided")

class CurrentUserDoesNotExistException : RuntimeException("Current user does not exist")

class UserDoesNotExistException(userId: Long) : RuntimeException("User $userId does not exist")

class MessageContentTooLongException: BadRequestException("Message content is too long")