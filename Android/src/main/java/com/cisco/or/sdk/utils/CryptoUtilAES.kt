package com.cisco.or.sdk.utils

import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


internal object CryptoUtilAES {
    private const val KEY_GENERATOR_METHOD = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS7Padding"
    private const val OFF_SET = 0

    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        ShortBufferException::class,
        UnsupportedEncodingException::class
    )
    fun decrypt(strToDecrypt: String?, key: String): String {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray
        keyBytes = key.toByteArray(Charsets.UTF_8)
        val skey = SecretKeySpec(keyBytes, KEY_GENERATOR_METHOD)
        val input = org.bouncycastle.util.encoders.Base64
            .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(Charsets.UTF_8))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, skey)

            val plainText = ByteArray(cipher.getOutputSize(input.size))
            var ptLength = cipher.update(input, OFF_SET, input.size, plainText, OFF_SET)
            ptLength += cipher.doFinal(plainText, ptLength)
            val decryptedString = String(plainText)
            return decryptedString.trim { it <= ' ' }
        }
    }

    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        ShortBufferException::class,
        UnsupportedEncodingException::class
    )
    fun encrypt(strToEncrypt: String, secret_key: String): String {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        keyBytes = secret_key.toByteArray(Charsets.UTF_8)
        val skey = SecretKeySpec(keyBytes, KEY_GENERATOR_METHOD)
        val input = strToEncrypt.toByteArray(Charsets.UTF_8)

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, skey)

            val cipherText = ByteArray(cipher.getOutputSize(input.size))
            var ctLength = cipher.update(
                input, OFF_SET, input.size,
                cipherText, OFF_SET
            )
            ctLength += cipher.doFinal(cipherText, ctLength)
            var rString = Base64.encodeToString(cipherText, Base64.DEFAULT)
            return rString.replace("\n", "")
        }
    }
}