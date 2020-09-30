package com.cisco.or.sdk

import com.cisco.or.sdk.utils.ValidateUtil
import org.junit.Assert
import org.junit.Test


internal class ValidateUtilTest {

    @Test
    fun emptyPhoneTest(){
        val phone = ""
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertFalse(result)
    }

    @Test
    fun emptyEmailTest(){
        val phone = ""
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertFalse(result)
    }

    @Test
    fun correctPhoneTest(){
        val phone = "998899775"
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertTrue(result)
    }

    @Test
    fun correctWithSpecialCharacterPhoneTest(){
        val phone = "+998-899-775"
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertTrue(result)
    }

    @Test
    fun wrongPhoneTest(){
        val phone = "99889a9775"
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertFalse(result)
    }

    @Test
    fun wrongWithSpecialCharacterPhoneTest(){
        val phone = "%998899775"
        val result = ValidateUtil.validatePhoneNumber(phone)
        Assert.assertFalse(result)
    }

    @Test
    fun correctEmailTest(){
        val email = "user@email.org.net"
        val result = ValidateUtil.validateEmail(email)
        Assert.assertTrue(result)
    }

    @Test
    fun wrongEmailTest(){
        val email = "useremail.com.br"
        val result = ValidateUtil.validateEmail(email)
        Assert.assertFalse(result)
    }

    @Test
    fun wrongNoFinalEmailTest(){
        val email = "user@email"
        val result = ValidateUtil.validateEmail(email)
        Assert.assertFalse(result)
    }
}