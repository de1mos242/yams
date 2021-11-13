package net.de1mos.yams

class DuplicateUsernameException(username: String) : RuntimeException("Username $username already exists")