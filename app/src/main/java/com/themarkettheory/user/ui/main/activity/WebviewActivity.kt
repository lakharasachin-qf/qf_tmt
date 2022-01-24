package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.themarkettheory.user.R
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.toolbar.*

class WebviewActivity : BaseActivity(), View.OnClickListener {
    var link: String? = ""
    var progressBar: ProgressBar? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        tvTitle.text = getString(R.string.privacy_policy)
        if (intent != null) {
            link = intent.getStringExtra("link")
        }

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        webview.settings.loadsImagesAutomatically = true
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;


        webview.settings.javaScriptEnabled = true
        /*webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.allowUniversalAccessFromFileURLs = true
        webview.settings.pluginState = WebSettings.PluginState.ON;*/
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false;
        webview.webViewClient = notreWebView()
        webview.loadUrl(link!!)

        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }

    inner class notreWebView : WebViewClient() {
        override fun onPageStarted(
            view: WebView,
            url: String,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            progressBar?.setVisibility(View.GONE)
            view.loadUrl(
                "javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()"
            )
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            progressBar?.setVisibility(View.VISIBLE)
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(
            view: WebView,
            url: String
        ) {
            super.onPageFinished(view, url)
            view.loadUrl(
                "javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()"
            )
        }
    }
}