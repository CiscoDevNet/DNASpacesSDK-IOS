package com.cisco.or.sdk

import com.cisco.or.sdk.utils.Constants
import com.cisco.or.sdk.utils.CryptoUtilAES
import org.junit.Before
import org.junit.Test
import java.security.InvalidKeyException


internal class CryptoUtilAESTest {

    private val text = "Test message to crypto!!!"
    private val invalidKey = "123456789123456789"
    private var encryptedMessage = ""
    private var decryptedMessage = ""
    private val secretKey = "662ede816988e58fb6d057d9d85605e0"

    @Before
    fun setUp(){
        encryptedMessage = CryptoUtilAES.encrypt(text, secretKey)
        decryptedMessage = CryptoUtilAES.decrypt(encryptedMessage, secretKey)
    }

    @Test
    fun decryptedMessageTest() {
        assert(decryptedMessage == text)
    }

    @Test
    fun encryptedMessageIsNotEmptyTest() {
        assert(!encryptedMessage.isNullOrBlank())
    }

    @Test
    fun encryptedMessageTest() {
        assert(encryptedMessage != text)
    }

    @Test(expected = InvalidKeyException::class)
    fun invalidKeyToEncrypt() {
        CryptoUtilAES.encrypt(text, invalidKey)
    }

    @Test(expected = InvalidKeyException::class)
    fun invalidKeyToDecrypt() {
        CryptoUtilAES.decrypt(encryptedMessage, invalidKey)
    }
}