package com.juno.temp.users.password

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PasswordChainValidatorTest {

    val VALIDATORS = listOf(TextValidator(), DuplicateCaseValidator(), ContinueStringValidator())

    @Test
    fun `특수문자를 포함하지 않으면 실패한다`() {
        //given
        val password = "abA47561"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `일반문자를 포함하지 않으면 실패한다`() {
        //given
        val password = "551122!$!"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `영소문자를 포함하지 않으면 실패한다`() {
        //given
        val password = "A551122!$!"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `영대문자를 포함하지 않으면 실패한다`() {
        //given
        val password = "a551122!$!"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `8글자 이하는 실패한다`() {
        //given
        val password = "aA522!$"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `동일한 영어 소문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "abaca"

        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `동일한 영어 대문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "AbcAbcA"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `동일한 특수문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "!abc!Abca!"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `동일한 숫자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "1abc1Abca1"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 영소문자 문자열을 입력하면 실패한다`() {
        //given
        val password = "qbcd"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 영대문자를 입력하면 실패한다`() {
        //given
        val password = "EABCE"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 숫자를 입력하면 실패한다`() {
        //given
        val password = "56234221"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `연속된 특수문자를 입력하면 실패한다`() {
        //given
        val password = "1122%^&"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isFalse()
    }

    @Test
    fun `모든 체인 조건을 만족해야 성공한다`() {
        //given
        val password = "aAb12!#@"
        //when
        val validate = PasswordChainValidator(VALIDATORS).validate(password)
        //then
        Assertions.assertThat(validate).isTrue()
    }
}