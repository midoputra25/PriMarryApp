package com.example.primarryapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.primarryapp.R  // Replace with your package name

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragmen_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        setupWebView()
        loadDialogflowChatbot()
    }

    private fun setupWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

    }

    private fun loadDialogflowChatbot() {
        val chatbotUrl = "http://34.74.112.122:5000/"
        webView.loadUrl(chatbotUrl)
    }
}
