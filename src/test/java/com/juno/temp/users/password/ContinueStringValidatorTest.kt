package com.juno.temp.users.password

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ContinueStringValidatorTest {

    @Test
    fun `연속된 영소문자 문자열을 입력하면 실패한다`() {
        //given
        val password = "qbcd"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 영대문자를 입력하면 실패한다`() {
        //given
        val password = "EABCE"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 숫자를 입력하면 실패한다`() {
        //given
        val password = "56234221"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 특수문자를 입력하면 실패한다`() {
        //given
        val password = "1122%^&"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속되지 않는 문자를 입력하면 성공한다`() {
        //given
        val password = "1122"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isTrue()
    }

    @Test
    fun `영대소문자는 연속되어도 성공한다`() {
        //given
        val password = "aBc1122"
        //when
        val validate = ContinueStringValidator().validate(password)
        //then
        Assertions.assertThat(validate).isTrue()
    }
}