package com.cisco.or.sdk

import android.util.Base64
import com.cisco.or.sdk.utils.CryptoUtil
import org.junit.Before
import org.junit.Test


internal class CryptoUtilTest {

    val text = "Test message to crypto!!!"
    var encryptedMessage = ""
    var decryptedMessage = ""

    @Before
    fun setUp(){
        //val keys = CryptoUtil.generateKeyPair()
        //val privateKey = Base64.encodeToString(keys.private.encoded, Base64.DEFAULT)
        //val publicKey = Base64.encodeToString(keys.public.encoded, Base64.DEFAULT)
        //encryptedMessage = CryptoUtil.encrypt(text, publicKey!!)
        val msg = "hAyllOnQkNFlKAztJz3G1IXLXIFuUI8ZUkcq53xdLQf1XOKXXeReK5omKEoQXawy+03GoQCCRKRPhd82mYxcC/yVI+QotLZZD066PPeLd3vRcmTvMNJl5WKpazJ/sJ90+qM+k/AS/w631ztGJv4QjsTFyLr4kdpxJrVAdAQLDaKe4uqxUzgjsXrCmN4bFMZMbKyH7YmR2tEyBeIA6rj80xUL82InGMCEs8gFdNVKF4dN/RIEdY7u9qGelL6ll2s5jKqv6tWslp0LcA/+emQYxD4WS20/jR+499rLfZQUn7OzXOfjwrkn2sHZoDAKxWlZ2NTwrgS4KkXtKruFO4prWyxLLmP24iANGmMPuvgRcA9evN+NwFdI+motW8eMST0B7N+0/JAaJX5++N9gfWtoOj115a3LBN8YPaGfd0JO0FlDVZeGTfaWkmNGhgVkscU/L8sf8zgSwX9LllovX09Lt4gj0Zm1pzXzyVddYYUxxwZO2PQ5QQCnNQJnlKfVlCjB"
        val pKey = "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"
        //decryptedMessage = CryptoUtil.decrypt(encryptedMessage, privateKey!!)
        val decrypted = CryptoUtil.decryptToPrivateKey(msg, pKey)
        val msg2 = "BFOivuIUncTNNGAxkwR6PLnoKENuuDnneHbvg9vAAJQiEMWdWOJoguC1Z8UsxJNtm5OS/tBfvbx2ww2zI7oh9vpZ4KTYcdg3hJoepYVqL+C6cPPPxilM8tz/vZ9nsjfbhxmSpamfTSmqNDS1vHREh9tyvnsUgSpxrHQ/+YSyt23PJN1BYp96H17NhtAWHIo5+wuj+u30M0ctL0o4UmkEgdYjE4CF/H4UCMDhXrJUQtbq/EKBgjw1zVpReoM/tYuRKTNwtcqZQ5hjw/9MKiVhKymjYd8EHszhZkXWt2jOxgcEnkmyrSGI2PCidPK9/tGbwEkk01tQCClFYl/M6x/m560Q5MQGguKxEB50jdU1/MTWtjKW2tEngSIeHlcVs5FTVOyBcXCl26DR8yksAlnKPBFsGiULtQc8W47pkerX0zEx2c6FW9zouqQf7rcwC9yKtvxZoCwEhP8F2BGTuOk3ZahOa0hAxN4UhcM9Pyy3/4T/MU7ae66Ww/jZjG4WvTZ2"
        val decrypted2 = CryptoUtil.decrypt(msg2,pKey)
        val encrypted = CryptoUtil.encrypt("Testando isso aqui!!!",Base64.encodeToString(decrypted, Base64.NO_PADDING))
        System.out.println(encrypted)
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
}