package com.leemyeongyun.simple_web_browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initViews()
        bindViews()
    }


    // 뒤로가기 버튼 함수
    override fun onBackPressed() {
        if (webView.canGoBack()) { //이전 페이지가 있다면
            webView.goBack()
        } else { //없다면
            super.onBackPressed() // 앱종료
        }
    }

    //명시된 url 불러오기
    @SuppressLint("SetJavaScriptEnabled")//경고 무시
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient() // webView에 load하게 함
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true //자바스크립트의 보안으로 인해 webView에 추가해야 사이트 기능을 이용할 수 있음
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addressBar.setOnEditorActionListener { v, actionId, event ->//주소창 입력액션
            if (actionId == EditorInfo.IME_ACTION_DONE) {//키보드가 닫히면
                val loadingUrl = v.text.toString() //주소를 받음
                if (URLUtil.isNetworkUrl(loadingUrl)) { // URLUtil로 앞에 http가 붙어있다면,
                    webView.loadUrl(loadingUrl)//해당 사이트로 이동
                } else// 없다면 http를 붙임
                    webView.loadUrl("http://$loadingUrl") // https를 지원하는 브라우저들은 http로 접근해도 https로 리다이렉팅이 기본임
            }
            return@setOnEditorActionListener false //true를 반환하게된다면 액션을 진행할 수가 없어서 키보드가 안닫아짐

        }

        goBackButton.setOnClickListener {
            webView.goBack() //이전 페이지로

        }

        goForwardButton.setOnClickListener {
            webView.goForward() //앞 페이지로

        }

        refreshLayout.setOnRefreshListener {
            webView.reload() // 새로고침
        }

    }

    //새로고침 표시 제거
    inner class WebViewClient : android.webkit.WebViewClient() { //inner로 상위 클래스 접근가능


        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show() //페이지가 시작될때 보여주게 처리
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide() //로딩이 완료되면 사라지게 처리
            goBackButton.isEnabled = webView.canGoBack() //뒤 페이지로 갈 수 있다면 누를 수 있게함.
            goForwardButton.isEnabled = webView.canGoForward() //앞 페이지로 갈 수 있다면 누를 수 있게함.
            addressBar.setText(url) // 로딩된 url 보여줌
        }
    }

    //브라우저 차원의 복잡한 이벤트를 처리하려면 WebChromeClient 이용
    inner class WebChromeClient : android.webkit.WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress // 로딩된 정도의 기본값, 0 ~ 100 그대로 저장
        }
    }

    //주소 상수화
    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}




