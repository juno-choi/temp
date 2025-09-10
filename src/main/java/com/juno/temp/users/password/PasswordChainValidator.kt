package com.juno.temp.users.password

class PasswordChainValidator (
    val validators: List<PasswordValidator>
) {
    fun validate(password: String): Boolean {
        return validators.all { it.validate(password) }
    }
}