package com.juno.temp.users.password

class DuplicateCaseValidator : PasswordValidator {
    override fun validate(password: String): Boolean {
        val groupingBy = password.groupingBy { it }.eachCount()
        return groupingBy.all { it.value < 3 }
    }
}