package com.cisco.or.sdk.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


internal object CryptoUtil {
    private const val KEY_GENERATOR_METHOD = "RSA"
    private const val TRANSFORMATION = "RSA"
    private const val CRYPTO_BITS = 2048

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun generateKeyPair(): KeyPair {
        val kpg = KeyPairGenerator.getInstance(KEY_GENERATOR_METHOD)
        kpg.initialize(CRYPTO_BITS)
        return kpg.genKeyPair()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeySpecException::class
    )
    fun encrypt(plain: String, pubk: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, stringToPublicKey(pubk))
        val encryptedBytes = cipher.doFinal(Base64.decode(plain, Base64.NO_WRAP))
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeySpecException::class
    )
    fun encryptPublicKey(clientPublicKey: String, serverPublicKey: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, stringToPublicKey(serverPublicKey))
        val encryptedBytes = cipher.doFinal(stringToPublicKey(clientPublicKey).encoded)
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeySpecException::class,
        InvalidKeyException::class
    )
    fun decrypt(result: String?, privk: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, stringToPrivateKey(privk))
        val decryptedBytes = cipher.doFinal(Base64.decode(result, Base64.DEFAULT))
        return String(decryptedBytes)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidKeySpecException::class,
        InvalidKeyException::class
    )
    fun decryptToPrivateKey(result: String?, privk: String): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, stringToPrivateKey(privk))
        val decryptedBytes = cipher.doFinal(Base64.decode(result, Base64.DEFAULT))
        return decryptedBytes
    }

    @Throws(
        InvalidKeySpecException::class,
        NoSuchAlgorithmException::class
    )
    private fun stringToPublicKey(publicKeyString: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_GENERATOR_METHOD)
        return keyFactory.generatePublic(spec)
    }

    @Throws(
        InvalidKeySpecException::class,
        NoSuchAlgorithmException::class
    )
    private fun stringToPrivateKey(privateKeyString: String): PrivateKey {
        val pkcs8EncodedBytes = Base64.decode(privateKeyString, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance(KEY_GENERATOR_METHOD)
        return kf.generatePrivate(keySpec)
    }
}