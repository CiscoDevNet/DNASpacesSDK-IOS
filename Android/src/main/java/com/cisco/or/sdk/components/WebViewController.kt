package com.cisco.or.sdk.components

import android.content.Context
import android.util.Log
import android.webkit.*
import com.cisco.or.sdk.enums.LoadingState
import com.cisco.or.sdk.enums.SigningState
import com.cisco.or.sdk.exceptions.LoginFailedException
import com.cisco.or.sdk.exceptions.TokenEmptyException
import com.cisco.or.sdk.types.LoadingHandler
import com.cisco.or.sdk.utils.Constants
import com.cisco.or.sdk.utils.SharedPrefs

internal class WebViewController(private val context: Context,
                                 private val loadingHandler: LoadingHandler,
                                 private val finish: () -> Unit)  : WebViewClient() {

    companion object {
        private val TAG: String = WebViewController::class.java.name

        fun configureWebView(vewView: WebView) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies{}
            vewView.settings.javaScriptEnabled = true
            vewView.settings.databaseEnabled = false
            vewView.settings.domStorageEnabled = false
            vewView.settings.allowContentAccess = false
            vewView.settings.userAgentString =
                vewView.settings.userAgentString.replace("; wv", "")
            vewView.settings.setAppCacheEnabled(false)
            vewView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            vewView.clearFormData()
            vewView.clearCache(true)
            vewView.clearHistory()
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }

    @Throws(TokenEmptyException::class, LoginFailedException::class)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        loadingHandler(LoadingState.STARTED)

        var url = request.url.toString()
        val prefs = SharedPrefs(context)
        if (url.contains(Constants.redirectURL)) {
            prefs.signingState = SigningState.SIGNED
            finish()
        } else {
            view.loadUrl(request.url.toString())
        }
        return false
    }



    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        loadingHandler(LoadingState.FINISHED)
    }
}