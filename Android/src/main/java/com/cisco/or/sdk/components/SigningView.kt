package com.cisco.or.sdk.components

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.RelativeLayout

/**
 * SigningView is responsible to render the signing page on the sign-in ot sign-up process.
 *
 * Include this view to your layout and pass its reference to OpenRoaming.signing() method to let it
 * guide the signing process.
 */
class SigningView (context: Context, attrs: AttributeSet): RelativeLayout(context, attrs) {

    internal val webView:WebView = WebView(context)
    internal val progressBar:ProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)

    init {
        webView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        val layout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layout.addRule(CENTER_IN_PARENT, TRUE)
        progressBar.layoutParams = layout

        addView(webView)
        addView(progressBar)
    }
}