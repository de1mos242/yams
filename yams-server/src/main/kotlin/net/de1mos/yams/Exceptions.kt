package net.de1mos.yams

class DuplicateUsernameException(username: String) : RuntimeException("Username $username already exists")

class SenderAndReceiverAreSameException : RuntimeException("Can't send message to self")

class SearchUserNotProvided : RuntimeException("User to search from not provided")

class CurrentUserDoesNotExistsException : RuntimeException("Current user does not exists")

class UserDoesNotExistsException(userId: Long) : RuntimeException("User $userId does not exists ")