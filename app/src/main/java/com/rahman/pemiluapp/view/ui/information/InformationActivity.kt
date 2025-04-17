package com.rahman.pemiluapp.view.ui.information

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.rahman.pemiluapp.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {
    private var _binding: ActivityInformationBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView
    private val onBackInvokedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this@InformationActivity, onBackInvokedCallback)
        supportActionBar?.hide()
        setupWebView()

        binding.appBar.setNavigationOnClickListener { finish() }
    }

    private fun setupWebView() {
        webView = binding.webView
        webView.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportZoom(false)
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }
            }

            loadUrl("https://infopemilu.kpu.go.id/")
        }
    }

    private fun backPressed() {
        if (webView.canGoBack()) webView.goBack()
        else finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}