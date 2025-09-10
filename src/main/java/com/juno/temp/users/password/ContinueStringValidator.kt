package com.juno.temp.users.password


class ContinueStringValidator : PasswordValidator {
    override fun validate(password: String): Boolean {
        val charArray = password.toCharArray()

        if (checkNormalCharacter(charArray)) {
            return@validate false
        }

        if (checkSpecialCharacter(charArray)) {
            return@validate false
        }

        return true
    }

    private fun checkNormalCharacter(charArray: CharArray): Boolean {
        for (i in 0 until charArray.size - 2) {
            val c = charArray[i]

            val character1 = c + 1
            val character2 = c + 2

            if (isContinueCharacter(charArray, i, character1, character2)) {
                return true
            }
        }

        return false
    }

    private fun isContinueCharacter(
        charArray: CharArray,
        i: Int,
        character1: Char,
        character2: Char
    ): Boolean {
        return charArray[i + 1] == character1 && charArray[i + 2] == character2
    }

    private fun checkSpecialCharacter(charArray: CharArray): Boolean {
        val specialCharacter1 = "~!@#$%^&*()_+"
        val specialCharacter2 = "<>?"
        val specialCharacter3 = ",./"
        val specialCharacter1Revers = specialCharacter1.reversed()
        val specialCharacter2Revers = specialCharacter2.reversed()
        val specialCharacter3Revers = specialCharacter3.reversed()

        for (i in 0 until charArray.size - 2) {
            val character1 = charArray[i]
            val character2 = charArray[i + 1]
            val character3 = charArray[i + 2]

            val checkCharacter = character1.toString() + character2.toString() + character3.toString()

            when {
                specialCharacter1.contains(checkCharacter) -> return true
                specialCharacter2.contains(checkCharacter) -> return true
                specialCharacter3.contains(checkCharacter) -> return true
                specialCharacter1Revers.contains(checkCharacter) -> return true
                specialCharacter2Revers.contains(checkCharacter) -> return true
                specialCharacter3Revers.contains(checkCharacter) -> return true
            }
        }

        return false
    }
}