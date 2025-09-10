package com.juno.temp.users.password

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TextValidatorTest {

    @Test
    fun `8자 이하는 실패한다`() {
        //given
        val password = "1234567"
        //when
        val result = TextValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `영소문자가 없으면 실패한다`() {
        //given
        val password = "A!12345678"
        //when
        val result = TextValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `영대문자가 없으면 실패한다`() {
        //given
        val password = "a!12345678"
        //when
        val result = TextValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `특수문자가 없으면 실패한다`() {
        //given
        val password = "aA12345678"
        //when
        val result = TextValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `8글자 이상, 특수문자, 영소문자, 영대문자를 포함하면 성공한다`() {
        //given
        val list = listOf("\\", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "{", "}", "[", "]", ":", ";", "'", ",", ".", "<", ">", "/", "?", "|", "₩", "|")
        var password = "aA12345678"

        //when & then
        for (s in list) {
            val checkPassword = password + s
            val validate = TextValidator().validate(checkPassword)
            Assertions.assertThat(validate).isTrue()
        }
    }
}