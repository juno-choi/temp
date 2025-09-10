package com.juno.temp.users.password

interface PasswordValidator {
    fun validate(password: String): Boolean
}