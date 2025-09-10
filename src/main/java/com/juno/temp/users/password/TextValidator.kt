package com.juno.temp.users.password

class TextValidator : PasswordValidator {
    override fun validate(password: String): Boolean {
        return password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\₩~!@#$%^&*(),.?\":|{}\\[\\]<>=_+/;`'-]).{8,}$"))
    }
}