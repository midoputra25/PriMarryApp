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
        val chatbotUrl = "https://web.powerva.microsoft.com/environments/Default-90affe0f-c2a3-4108-bb98-6ceb4e94ef15/bots/cr261_copilot1kltXbs/webchat?_version_=2"
        webView.loadUrl(chatbotUrl)
    }
}
