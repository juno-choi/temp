package com.juno.temp.users.password

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class DuplicateCaseValidatorTest {

    @Test
    fun `동일한 영어 소문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "abaca"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `동일한 영어 대문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "AbcAbcA"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `동일한 특수문자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "!abc!Abca!"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `동일한 숫자가 3번 이상 입력되면 실패한다`() {
        //given
        val password = "1abc1Abca1"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    fun `모든 글자를 3번 이하로 사용하면 성공한다`() {
        //given
        val password = "abcdefg"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isTrue()
    }

    @Test
    fun `영어 대소문자는 구분하지 않아 3번 이상 입력되어도 성공한다`() {
        //given
        val password = "AbcAbca"
        //when
        val result = DuplicateCaseValidator().validate(password)
        //then
        Assertions.assertThat(result).isTrue()
    }

}